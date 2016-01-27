package org.opencb.opencga.storage.hadoop.variant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.datastore.core.ObjectMap;
import org.opencb.opencga.storage.core.StorageManagerException;
import org.opencb.opencga.storage.core.StudyConfiguration;
import org.opencb.opencga.storage.core.variant.VariantStorageManager;
import org.opencb.opencga.storage.core.variant.VariantStorageManagerTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created on 21/01/16
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantHadoopMultiSampleTest extends HadoopVariantStorageManagerTestUtils {

    private VariantHadoopDBAdaptor dbAdaptor;
    private VariantSource source1;
    private VariantSource source2;
    private StudyConfiguration studyConfiguration = null;

    @Before
    public void loadSingleVcf() throws Exception {
        if (studyConfiguration == null) {
            clearDB(DB_NAME);
            HadoopVariantStorageManager variantStorageManager = getVariantStorageManager();

            source1 = loadFile(variantStorageManager, "s1.genome.vcf", 1);
            source2 = loadFile(variantStorageManager, "s2.genome.vcf", 2);

            dbAdaptor = variantStorageManager.getDBAdaptor(DB_NAME);

        }
    }

    public VariantSource loadFile(HadoopVariantStorageManager variantStorageManager, String resourceName, int fileId) throws IOException, URISyntaxException, FileFormatException, StorageManagerException {
        URI fileInputUri = VariantStorageManagerTestUtils.getResourceUri(resourceName);
        if (studyConfiguration == null) {
            studyConfiguration = VariantStorageManagerTestUtils.newStudyConfiguration();
        }
        ETLResult etlResult = VariantStorageManagerTestUtils.runDefaultETL(fileInputUri, variantStorageManager, studyConfiguration,
                new ObjectMap(VariantStorageManager.Options.TRANSFORM_FORMAT.key(), "avro")
                        .append(VariantStorageManager.Options.ANNOTATE.key(), false)
                        .append(VariantStorageManager.Options.CALCULATE_STATS.key(), false)
                        .append(VariantStorageManager.Options.FILE_ID.key(), fileId)
                        .append(HadoopVariantStorageManager.HADOOP_LOAD_ARCHIVE, true)
                        .append(HadoopVariantStorageManager.HADOOP_LOAD_VARIANT, true)
        );
        return variantStorageManager.readVariantSource(etlResult.transformResult, new ObjectMap());
    }

    @Test
    public void test() {
        System.out.println("studyConfiguration = " + studyConfiguration);
        Map<String, Variant> variants = new HashMap<>();
        for (Variant variant : dbAdaptor) {
            String v = variant.toString();
            assertFalse(variants.containsKey(v));
            variants.put(v, variant);
            VariantAnnotation a = variant.getAnnotation();
            variant.setAnnotation(null);
            System.out.println(variant.toJson());
            variant.setAnnotation(a);
        }
        String studyName = studyConfiguration.getStudyName();

        // TODO: Add more asserts
        /*                      s1  s2
        1	10013	.	T	C   0/1 0/0
        1	10014	.	A	T   0/1 0/2
        1	10014	.	A	G   0/2 0/1
        1	10030	.	T	G   0/0 0/1
        1	10031	.	T	G   0/1 0/1
        1	10032	.	A	G   0/1 0/0
        */

        assertEquals(6, variants.size());
        assertTrue(variants.containsKey("1:10013:T:C"));
        assertEquals("0/1", variants.get("1:10013:T:C").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals("0/0", variants.get("1:10013:T:C").getStudy(studyName).getSampleData("s2", "GT"));

        assertTrue(variants.containsKey("1:10014:A:T"));
        assertEquals("0/1", variants.get("1:10014:A:T").getStudy(studyName).getSampleData("s1", "GT"));
//        assertEquals("0/2", variants.get("1:10014:A:T").getStudy(studyName).getSampleData("s2", "GT"));
        assertEquals(".", variants.get("1:10014:A:T").getStudy(studyName).getSampleData("s2", "GT"));

        assertTrue(variants.containsKey("1:10014:A:G"));
//        assertEquals("0/2", variants.get("1:10014:A:G").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals(".", variants.get("1:10014:A:G").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals("0/1", variants.get("1:10014:A:G").getStudy(studyName).getSampleData("s2", "GT"));

        assertTrue(variants.containsKey("1:10030:T:G"));
        assertEquals("0/0", variants.get("1:10030:T:G").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals("0/1", variants.get("1:10030:T:G").getStudy(studyName).getSampleData("s2", "GT"));

        assertTrue(variants.containsKey("1:10031:T:G"));
        assertEquals("0/1", variants.get("1:10031:T:G").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals("0/1", variants.get("1:10031:T:G").getStudy(studyName).getSampleData("s2", "GT"));

        assertTrue(variants.containsKey("1:10032:A:G"));
        assertEquals("0/1", variants.get("1:10032:A:G").getStudy(studyName).getSampleData("s1", "GT"));
        assertEquals("0/0", variants.get("1:10032:A:G").getStudy(studyName).getSampleData("s2", "GT"));

    }
}