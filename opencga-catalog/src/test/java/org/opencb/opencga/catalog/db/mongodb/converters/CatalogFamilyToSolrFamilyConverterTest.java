package org.opencb.opencga.catalog.db.mongodb.converters;

import org.junit.Test;
import org.opencb.opencga.catalog.stats.solr.FamilySolrModel;
import org.opencb.opencga.catalog.stats.solr.converters.CatalogFamilyToSolrFamilyConverter;
import org.opencb.opencga.core.models.Family;
import org.opencb.opencga.core.models.Individual;
import org.opencb.opencga.core.models.Study;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by wasim on 13/08/18.
 */
public class CatalogFamilyToSolrFamilyConverterTest {

    @Test
    public void FamilyToSolrTest() {
        Study study = new Study().setFqn("user@project:study").setAttributes(new HashMap<>());
        Family family = new Family("id", "family", null, Arrays.asList(new Individual().setId("I1"), new Individual().setId("I2")),
                "test", 1000, AnnotationHelper.createAnnotation(), null);
        family.setUid(100).setStatus(new Family.FamilyStatus("READY")).setRelease(1).setVersion(2);
        FamilySolrModel familySolrModel = new CatalogFamilyToSolrFamilyConverter(study).convertToStorageType(family);

        assertEquals(familySolrModel.getUid(), family.getUid());
        assertEquals(familySolrModel.getStatus(), family.getStatus().getName());
        assertEquals(familySolrModel.getNumMembers(), family.getMembers().size());
        assertEquals(familySolrModel.getRelease(), family.getRelease());
        assertEquals(familySolrModel.getVersion(), family.getVersion());
        assertEquals(familySolrModel.getPhenotypes().size(), 0);

        assertEquals(familySolrModel.getAnnotations().get("annotations__o__annotName.vsId.a.ab2.ab2c1.ab2c1d1"), Arrays.asList(1, 2, 3, 4, 11, 12, 13, 14, 21));
        assertEquals(familySolrModel.getAnnotations().get("annotations__o__annotName.vsId.a.ab1.ab1c1"), Arrays.asList(true, false, false));
        assertEquals(familySolrModel.getAnnotations().get("annotations__s__annotName.vsId.a.ab1.ab1c2"), "hello world");
        assertEquals(familySolrModel.getAnnotations().get("annotations__o__annotName.vsId.a.ab2.ab2c1.ab2c1d2"), Arrays.asList("hello ab2c1d2 1", "hello ab2c1d2 2"));
        assertEquals(familySolrModel.getAnnotations().get("annotations__o__annotName.vsId.a.ab3.ab3c1.ab3c1d1"), Arrays.asList(Arrays.asList("hello"), Arrays.asList("hello2", "bye2"), Arrays.asList("byeee2", "hellooo2")));
        assertEquals(familySolrModel.getAnnotations().get("annotations__o__annotName.vsId.a.ab3.ab3c1.ab3c1d2"), Arrays.asList(2.0, 4.0, 24.0));
        assertNull(familySolrModel.getAnnotations().get("nothing"));
        assertEquals(familySolrModel.getAnnotations().keySet().size(), 6);

    }
}
