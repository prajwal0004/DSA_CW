public class StrongPinValidator {

    public static int minStepsToStrongPIN(String pin_code) {
        int len = pin_code.length();

        boolean hasLower = false, hasUpper = false, hasDigit = false;
        int repeatChanges = 0;

        // Check character types and find repeating sequences
        for (int i = 0; i < len; ) {
            char ch = pin_code.charAt(i);

            if (Character.isLowerCase(ch)) hasLower = true;
            if (Character.isUpperCase(ch)) hasUpper = true;
            if (Character.isDigit(ch)) hasDigit = true;

            int j = i;
            while (j < len && pin_code.charAt(j) == ch) j++;

            int repeatLen = j - i;
            if (repeatLen >= 3) {
                repeatChanges += repeatLen / 3;
            }

            i = j;
        }

        int missingTypes = 0;
        if (!hasLower) missingTypes++;
        if (!hasUpper) missingTypes++;
        if (!hasDigit) missingTypes++;

        if (len < 6) {
            return Math.max(6 - len, Math.max(missingTypes, repeatChanges));
        } else if (len <= 20) {
            return Math.max(missingTypes, repeatChanges);
        } else {
            int deleteSteps = len - 20;
            int remainingRepeatChanges = reduceRepeatsWithDeletions(pin_code, deleteSteps);
            return deleteSteps + Math.max(missingTypes, remainingRepeatChanges);
        }
    }

    private static int reduceRepeatsWithDeletions(String pin, int deletions) {
        int[] repeatLens = new int[pin.length()];
        int idx = 0;

        for (int i = 0; i < pin.length(); ) {
            int j = i;
            while (j < pin.length() && pin.charAt(j) == pin.charAt(i)) j++;
            repeatLens[idx++] = j - i;
            i = j;
        }

        int changes = 0;

        for (int k = 0; k < idx; k++) {
            int len = repeatLens[k];
            if (len < 3) continue;

            if (deletions > 0) {
                int reduce = Math.min(deletions, len - 2);
                len -= reduce;
                deletions -= reduce;
            }

            changes += len / 3;
        }

        return changes;
    }

    public static void main(String[] args) {
        System.out.println(minStepsToStrongPIN("X1!"));       // Output: 3
        System.out.println(minStepsToStrongPIN("123456"));    // Output: 2
        System.out.println(minStepsToStrongPIN("Aa1234!"));   // Output: 0
    }
}
