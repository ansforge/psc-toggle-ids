/*
 * psc-api-maj-v2
 * API CRUD for Personnels et Structures de santé
 *
 * OpenAPI spec version: 1.0
 * Contact: superviseurs.psc@esante.gouv.fr
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package fr.ans.psc.api;

import fr.ans.psc.model.Error;
import fr.ans.psc.model.Structure;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for StructureApi
 */
@Ignore
public class StructureApiTest {

    private final StructureApi api = new StructureApi();

    /**
     * Create new structure
     *
     * Create a new structure
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void createNewStructureTest() {
        Structure body = null;
        api.createNewStructure(body);

        // TODO: test validations
    }
    /**
     * Delete structure by id
     *
     * Delete structure by structureId
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteStructureByStructureIdTest() {
        String structureId = null;
        api.deleteStructureByStructureId(structureId);

        // TODO: test validations
    }
    /**
     * Get structure by id
     *
     * Get structure by id
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getStructureByIdTest() {
        String structureId = null;
        Structure response = api.getStructureById(structureId);

        // TODO: test validations
    }
    /**
     * Update structure
     *
     * Update structure
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void updateStructureTest() {
        Structure body = null;
        api.updateStructure(body);

        // TODO: test validations
    }
}
