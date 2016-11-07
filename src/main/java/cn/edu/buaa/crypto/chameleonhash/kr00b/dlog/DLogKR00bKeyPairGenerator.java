package cn.edu.buaa.crypto.chameleonhash.kr00b.dlog;

import cn.edu.buaa.crypto.algebra.params.SecurePrimeParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Weiran Liu on 2016/10/19.
 *
 * Krawczyk-Rabin Chameleon hash.key pair generator.
 */
public class DLogKR00bKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);

    private DLogKR00bKeyGenerationParameters param;

    public void init(KeyGenerationParameters param) {
        this.param = (DLogKR00bKeyGenerationParameters) param;
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        SecurePrimeParameters securePrimeParameters = param.getParameters();

        BigInteger x = generatePrivateKey(securePrimeParameters.getQ(), param.getRandom());
        BigInteger y = calculatePublicKey(securePrimeParameters.getP(), securePrimeParameters.getG(), x);

        return new AsymmetricCipherKeyPair(
                new DLogKR00bPublicKeyParameters(y, securePrimeParameters),
                new DLogKR00bSecretKeyParameters(x, securePrimeParameters));
    }

    private static BigInteger generatePrivateKey(BigInteger q, SecureRandom random) {
        // B.1.2 Key Pair Generation by Testing Candidates
        int minWeight = q.bitLength() >>> 2;
        for (; ; ) {
            BigInteger x = BigIntegers.createRandomInRange(ONE, q.subtract(ONE), random);
            if (WNafUtil.getNafWeight(x) >= minWeight) {
                return x;
            }
        }
    }

    private static BigInteger calculatePublicKey(BigInteger p, BigInteger g, BigInteger x) {
        return g.modPow(x, p);
    }
}