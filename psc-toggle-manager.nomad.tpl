job "psc-toggle-manager" {
  datacenters = ["${datacenter}"]
  type = "service"
  namespace = "${nomad_namespace}"
  
  vault {
    policies = ["psc-ecosystem"]
    change_mode = "restart"
  }

  group "pscload-toggle" {
    count = "1"
    restart {
      attempts = 3
      delay = "60s"
      interval = "1h"
      mode = "fail"
    }

    update {
      max_parallel = 1
      min_healthy_time = "30s"
      progress_deadline = "5m"
      healthy_deadline = "2m"
    }

    network {
      port "http" {
        to = 8080
      }
    }

    task "toggle" {
      driver = "docker"
      config {
        image = "${artifact.image}:${artifact.tag}"
        ports = [
          "http"]
      }

      template {
        destination = "local/file.env"
        env = true
        data = <<EOH
JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m -XX:+UseG1GC -Dspring.config.location=/secrets/application.properties -Dhttps.proxyHost=${proxy_host} -Dhttps.proxyPort=${proxy_port} -Dhttps.nonProxyHosts=${non_proxy_hosts}"
PUBLIC_HOSTNAME={{ with secret "psc-ecosystem/${nomad_namespace}/pscload" }}{{ .Data.data.public_hostname }}{{ end }}
EOH
      }

      template {
        data = <<EOF
server.servlet.context-path=/toggle/v1
api.base.url=http://{{ range service "${nomad_namespace}-psc-api-maj-v2" }}{{ .Address }}:{{ .Port }}{{ end }}/psc-api-maj/api
spring.servlet.multipart.max-file-size=60MB
spring.servlet.multipart.max-request-size=60MB
{{ range service "${nomad_namespace}-psc-rabbitmq" }}
spring.rabbitmq.host={{ .Address }}
spring.rabbitmq.port={{ .Port }}{{ end }}
spring.rabbitmq.username={{ with secret "psc-ecosystem/${nomad_namespace}/rabbitmq" }}{{ .Data.data.user }}
spring.rabbitmq.password={{ .Data.data.password }}{{ end }}
spring.mail.host={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_server_host }}{{ end }}
spring.mail.port={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_server_port }}{{ end }}
spring.mail.username={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_username }}{{ end }}
spring.mail.password={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_password }}{{ end }}
spring.mail.properties.mail.smtp.auth={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_smtp_auth }}{{ end }}
spring.mail.properties.mail.smtp.starttls.enable={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_enable_tls }}{{ end }}
toggle.mail.receiver={{ with secret "psc-ecosystem/${nomad_namespace}/admin" }}{{ .Data.data.mail_receiver }}{{ end }}
enable.emailing=true
EOF
        destination = "secrets/application.properties"
        change_mode = "restart"
      }

      resources {
        cpu = 300
        memory = 1024
      }

      service {
        name = "$\u007BNOMAD_NAMESPACE\u007D-$\u007BNOMAD_JOB_NAME\u007D"
        tags = ["urlprefix-$\u007BPUBLIC_HOSTNAME\u007D/toggle/"]
        port = "http"
        check {
          type = "http"
          path = "/toggle/v1/check"
          port = "http"
          interval = "30s"
          timeout = "2s"
          failures_before_critical = 5
        }
      }

    }
  }
}
