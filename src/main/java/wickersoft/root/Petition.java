package wickersoft.root;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Petition {

    private static final char[] HEX_CHARS = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};
    private static MessageDigest SHA1;
    private final HashSet<String> signatures;
    private final String name;

    private static String hexString(byte[] binary) {
        char[] hex = new char[binary.length * 2];
        for (int i = 0; i < binary.length; i++) {
            hex[2 * i] = HEX_CHARS[(binary[i] >> 4) & 0xF];
            hex[2 * i + 1] = HEX_CHARS[binary[i] & 0xF];
        }
        return new String(hex);
    }
    
    public Petition(String name) {
        this.name = name;
        signatures = new HashSet<>();
    }

    public Petition(String name, Collection<String> signatures) {
        this(name);
        this.signatures.addAll(signatures);
    }

    public boolean signOrRevoke(UUID uuid) { // Shittiest salted hash ever!
        SHA1.reset();
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        SHA1.update(ByteBuffer.allocate(16).putLong(hi).putLong(lo).array());
        String signature = hexString(SHA1.digest(name.getBytes()));
        if(signatures.contains(signature)) {
            signatures.remove(signature);
            return false;
        } else {
            signatures.add(signature);
            return true;
        }
    }
    
    public List<String> getSignatures() {
        ArrayList<String> sigList = new ArrayList<>();
        sigList.addAll(signatures);
        return sigList;
    }
    
    public int getNumberOfSignatures() {
        return signatures.size();
    }

    static {
        try {
            SHA1 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
        }
    }
}
