package ch1mp.hagfish;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

import ch1mp.hagfish.utils.Crypter;
import ch1mp.hagfish.utils.Memory;
import ch1mp.hagfish.utils.Vault;

public class MemoryTest {

    private static final String PASSWORD = "password1234";
    private static final String TEST_FILE = "mem_test.dat";
    Vault vault;

    @Before
    public void setUpVault(){ vault = TestUtilities.createVault(); }

    @Test
    public void testMemoryConstructor()
    {
        File checkFile = new File(TEST_FILE);
        if(checkFile.exists()) checkFile.delete();

        Memory memory = Memory.getInstance(TEST_FILE);
        Crypter crypter = new Crypter(PASSWORD);

        assertNull(memory.getVault(crypter));
        assertEquals(5d, (double) memory.getRemainingAttempts(), 0d);

        memory.saveMemory(TEST_FILE, vault, crypter);
        memory = null;
        memory = Memory.getInstance(TEST_FILE);

        Vault loadedVault = memory.getVault(crypter);
        assertNotNull(loadedVault);
        assertTrue(TestUtilities.testVaultContents(vault, "NetflixStanDisney+"));
    }


}
