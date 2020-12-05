package ansi.x9_24_2004.dukpt;

import ansi.x9_24_2004.encryption.Des;
import ansi.x9_24_2004.encryption.TripleDes;
import ansi.x9_24_2004.utils.CustomBitSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class DukptTest {

    private static final CustomBitSet BDK = CustomBitSet.toBitSet("BDBD1234BDBD567890ABBDBDCDEFBDBD");
    private static final CustomBitSet KSN = CustomBitSet.toBitSet("FFFF9876543210E01E9D");

    private ansi.x9_24_2004.dukpt.Dukpt dukpt;

    @BeforeEach
    void init() {
        this.dukpt = new ansi.x9_24_2004.dukpt.Dukpt(new Des(), new TripleDes());
    }

    @Nested
    class WhenGetIpekMethodIsCalled {

        @Test
        void shouldCreateExpectedIpek() {
            // Given
            // When
            final CustomBitSet ipek = dukpt.getIpek(BDK, KSN);

            // Then
            Assertions.assertEquals("1B90D9C9AEE356ADF9938F6084D16C44", CustomBitSet.toString(ipek));
        }

    }

    @Nested
    class WhenGetTransactionKeyMethodIsCalled {

        @Test
        void shouldCreateExpectedDukpt() {
            // Given
            final CustomBitSet ipek = CustomBitSet.toBitSet("1B90D9C9AEE356ADF9938F6084D16C44");

            // When
            final CustomBitSet transactionKey = dukpt.getCurrentKey(ipek, KSN);

            // Then
            Assertions.assertEquals("0258F3E777F55F61241AE65234583B30", CustomBitSet.toString(transactionKey));
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class WhenComputeKeyVariantMethodIsCalled {

        @ParameterizedTest(name = "Should create key {1} for mask {0}.")
        @MethodSource("getMaskAndExpectedKey")
        void shouldComputeExpectedKeyVariant(final ansi.x9_24_2004.dukpt.Mask mask, final String expectedKey) {
            // Given
            // When
            final CustomBitSet actualKey = dukpt.computeKey(BDK, KSN, mask);

            // Then
            Assertions.assertEquals(expectedKey, CustomBitSet.toString(actualKey));
        }

        Stream<Arguments> getMaskAndExpectedKey() {
            return Stream.of(
                    Arguments.of(ansi.x9_24_2004.dukpt.Mask.REQUEST_DATA_MASK, "0258F3E7770A5F61241AE65234A73B30"),
                    Arguments.of(ansi.x9_24_2004.dukpt.Mask.REQUEST_MAC_MASK, "0258F3E777F5A061241AE6523458C430"),
                    Arguments.of(ansi.x9_24_2004.dukpt.Mask.PIN_MASK, "0258F3E777F55F9E241AE65234583BCF")
            );
        }
    }

}
