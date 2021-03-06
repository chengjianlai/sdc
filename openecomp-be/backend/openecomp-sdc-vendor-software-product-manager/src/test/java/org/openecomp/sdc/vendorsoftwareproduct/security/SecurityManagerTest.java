package org.openecomp.sdc.vendorsoftwareproduct.security;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityManager.class)
@PowerMockIgnore("javax.security.auth.x500.X500Principal")
public class SecurityManagerTest {
    private File certDir;
    private SecurityManager securityManager;

    @Before
    public void setUp() throws IOException {
        certDir = new File("/tmp/cert");
        if(certDir.exists()){
            tearDown();
        }
        certDir.mkdirs();
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv(eq("SDC_CERT_DIR"))).thenReturn(certDir.getPath());
        securityManager = SecurityManager.getInstance();
    }

    @After
    public void tearDown() throws IOException {
        if(certDir.exists()) {
            FileUtils.deleteDirectory(certDir);
        }
        securityManager.cleanTrustedCertificates();
    }

    @Test
    public void testGetCertificates() throws IOException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root-certificate.pem");
        File newFile = new File("/tmp/cert/root-certificate.pem");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        assertEquals(1, securityManager.getTrustedCertificates().size());
        newFile.delete();
        assertEquals(0, securityManager.getTrustedCertificates().size());
    }

    @Test
    public void testGetCertificatesNoDirectory() throws IOException, SecurityManagerException {
        certDir.delete();
        assertEquals(0, securityManager.getTrustedCertificates().size());
    }

    @Test(expected = SecurityManagerException.class)
    public void testGetCertificatesException() throws IOException, SecurityManagerException {
        File newFile = new File("/tmp/cert/root-certificate.pem");
        newFile.createNewFile();
        assertEquals(1, securityManager.getTrustedCertificates().size());
        newFile.delete();
        assertEquals(0, securityManager.getTrustedCertificates().size());
    }

    @Test
    public void testGetCertificatesUpdated() throws IOException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root-certificate.pem");
        File newFile = new File("/tmp/cert/root-certificate.pem");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        assertTrue(securityManager.getTrustedCertificates().size() == 1);
        File otherOrigFile = new File("src/test/resources/cert/package-certificate.pem");
        File otherNewFile = new File("/tmp/cert/package-certificate.pem");
        newFile.createNewFile();
        FileUtils.copyFile(otherOrigFile, otherNewFile);
        assertEquals(2, securityManager.getTrustedCertificates().size());
        otherNewFile.delete();
        assertEquals(1, securityManager.getTrustedCertificates().size());
        newFile.delete();
        assertEquals(0, securityManager.getTrustedCertificates().size());
    }

    @Test
    public void verifySignedDataTestCertIncludedIntoSignature() throws IOException, URISyntaxException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root.cert");
        File newFile = new File("/tmp/cert/root.cert");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        byte[] signature = Files.readAllBytes(Paths.get(getClass().getResource("/cert/2-file-signed-package/dummyPnfv4.cms").toURI()));
        byte[] archive = Files.readAllBytes(Paths.get(getClass().getResource("/cert/2-file-signed-package/dummyPnfv4.csar").toURI()));
        assertTrue(securityManager.verifySignedData(signature, null, archive));
    }

    @Test(expected = SecurityManagerException.class)
    public void verifySignedDataTestCertNotIncludedIntoSignatureButExpected() throws IOException, URISyntaxException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root.cert");
        File newFile = new File("/tmp/cert/root.cert");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        byte[] signature = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.cms").toURI()));
        byte[] archive = Files.readAllBytes(Paths.get(getClass().getResource("/cert/2-file-signed-package/dummyPnfv4.csar").toURI()));
        securityManager.verifySignedData(signature, null, archive);
    }

    @Test
    public void verifySignedDataTestCertNotIncludedIntoSignature() throws IOException, URISyntaxException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root.cert");
        File newFile = new File("/tmp/cert/root.cert");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        byte[] signature = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.cms").toURI()));
        byte[] archive = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.csar").toURI()));
        byte[] cert = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.cert").toURI()));
        assertTrue(securityManager.verifySignedData(signature, cert, archive));
    }

    @Test(expected = SecurityManagerException.class)
    public void verifySignedDataTestWrongCertificate() throws IOException, URISyntaxException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root-certificate.pem");
        File newFile = new File("/tmp/cert/root-certificate.cert");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        byte[] signature = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.cms").toURI()));
        byte[] archive = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.csar").toURI()));
        byte[] cert = Files.readAllBytes(Paths.get(getClass().getResource("/cert/3-file-signed-package/dummyPnfv4.cert").toURI()));
        securityManager.verifySignedData(signature, cert, archive);
    }

    @Test(expected = SecurityManagerException.class)
    public void verifySignedDataTestChangedArchive() throws IOException, URISyntaxException, SecurityManagerException {
        File origFile = new File("src/test/resources/cert/root.cert");
        File newFile = new File("/tmp/cert/root.cert");
        newFile.createNewFile();
        FileUtils.copyFile(origFile, newFile);
        byte[] signature = Files.readAllBytes(Paths.get(getClass().getResource("/cert/tampered-signed-package/dummyPnfv4.cms").toURI()));
        byte[] archive = Files.readAllBytes(Paths.get(getClass().getResource("/cert/tampered-signed-package/dummyPnfv4.csar").toURI()));
        securityManager.verifySignedData(signature, null, archive);
    }
}
