/*
 * Copyright (C) 2022-2024 Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
project = "prosanteconnect/${workspace.name}/psc-toggle-manager"

# Labels can be specified for organizational purposes.
labels = {
  "domaine" = "psc"
}

runner {
  enabled = true
  profile = "secpsc-${workspace.name}"
  data_source "git" {
    url = "https://github.com/ansforge/psc-ps-api.git"
    ref = "${workspace.name}"
  }
  poll {
    enabled = false
  }
}

# An application to deploy.
app "prosanteconnect/psc-toggle-manager" {
  # Build specifies how an application should be deployed. In this case,
  # we'll build using a Dockerfile and keeping it in a local registry.
  build {
    use "docker" {
      build_args         = {"PROSANTECONNECT_PACKAGE_GITHUB_TOKEN" = "${var.github_token}" }
      disable_entrypoint = true
    }
    # Uncomment below to use a remote docker registry to push your built images.
    registry {
      use "docker" {
        image    = "${var.registry_username}/psc-toggle-manager"
        tag      = gitrefpretty()
        username = var.registry_username
        password = var.registry_password
      }
    }
  }

  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/psc-toggle-manager.nomad.tpl", {
        datacenter      = var.datacenter
        nomad_namespace = var.nomad_namespace
        registry_path   = var.registry_username
      })
    }
  }
}

variable "datacenter" {
  type    = string
  default = ""
  env     = ["NOMAD_DATACENTER"]
}

variable "nomad_namespace" {
  type    = string
  default = ""
  env     = ["NOMAD_NAMESPACE"]
}

variable "registry_username" {
  type      = string
  default   = ""
  env       = ["REGISTRY_USERNAME"]
  sensitive = true
}

variable "registry_password" {
  type      = string
  default   = ""
  env       = ["REGISTRY_PASSWORD"]
  sensitive = true
}

variable "github_token" {
  type      = string
  default   = ""
  env       = ["PROSANTECONNECT_PACKAGE_GITHUB_TOKEN"]
  sensitive = true
}
