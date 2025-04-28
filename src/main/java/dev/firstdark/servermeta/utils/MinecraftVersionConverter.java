package dev.firstdark.servermeta.utils;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HypherionSA
 * Helper Class to convert Minecraft Version numbers into valid SemVer versions
 */
public class MinecraftVersionConverter {

    final static Pattern RELEASE_PATTERN = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?$", Pattern.CASE_INSENSITIVE);
    final static Pattern SNAPSHOT_PATTERN = Pattern.compile("(\\d{2})w(\\d{2})(\\w)", Pattern.CASE_INSENSITIVE);

    public static DefaultArtifactVersion parse(String input) {

        if (RELEASE_PATTERN.matcher(input).matches())
            return new DefaultArtifactVersion(input);

        Matcher snapshot = SNAPSHOT_PATTERN.matcher(input);

        if (snapshot.matches()) {
            if (input.equalsIgnoreCase("15w14a")) {
                return new DefaultArtifactVersion("1.5.1-alpha.13.12.a");
            }

            String prefix = getSnapshotPrefix(snapshot.group(1), snapshot.group(2));
            if (prefix == null)
                throw new RuntimeException("Could not parse prefix for " + input);

            return new DefaultArtifactVersion(String.format("%s-SNAPSHOT.%s.%s.%s", prefix, snapshot.group(1), snapshot.group(2), snapshot.group(3)));
        }

        if (input.contains("-pre")) {
            String rel = input.substring(input.length() - 1);
            input = input.substring(0, input.indexOf("-pre"));
            return new DefaultArtifactVersion(String.format("%s-PRERELEASE.%s", input, rel));
        }

        if (input.contains(" Pre-Release ")) {
            String rel = input.substring(input.length() - 1);
            input = input.substring(0, input.indexOf(" Pre-Release "));
            return new DefaultArtifactVersion(String.format("%s-PRERELEASE.%s", input, rel));
        }

        if (input.contains("-rc")) {
            String rel = input.substring(input.length() - 1);
            input = input.substring(0, input.indexOf("-rc"));
            return new DefaultArtifactVersion(String.format("%s-rc.%s", input, rel));
        }

        String special = normalizeSpecialVersion(input);

        if (special != null) {
            return new DefaultArtifactVersion(special);
        }

        throw new RuntimeException("Invalid Version " + input);
    }

    private static String getSnapshotPrefix(String y, String w) {
        int year = Integer.parseInt(y);
        int week = Integer.parseInt(w);

        if (year == 25 && week >= 15 || year > 25) {
            return "1.21.6";
        } else if (year == 25 && week >= 2 && week <= 10) {
            return "1.21.5";
        } else if (year == 24 && week >= 44) {
            return "1.21.4";
        } else if (year == 24 && week >= 33 && week <= 40) {
            return "1.21.2";
        } else if (year == 24 && week >= 18 && week <= 21) {
            return "1.21";
        } else if (year == 23 && week >= 51 || year == 24 && week <= 14) {
            return "1.20.5";
        } else if (year == 23 && week >= 40 && week <= 46) {
            return "1.20.3";
        } else if (year == 23 && week >= 31 && week <= 35) {
            return "1.20.2";
        } else if (year == 23 && week >= 12 && week <= 18) {
            return "1.20";
        } else if (year == 23 && week <= 7) {
            return "1.19.4";
        } else if (year == 22 && week >= 42) {
            return "1.19.3";
        } else if (year == 22 && week == 24) {
            return "1.19.1";
        } else if (year == 22 && week >= 11 && week <= 19) {
            return "1.19";
        } else if (year == 22 && week >= 3 && week <= 7) {
            return "1.18.2";
        } else if (year == 21 && week >= 37 && week <= 44) {
            return "1.18";
        } else if (year == 20 && week >= 45 || year == 21 && week <= 20) {
            return "1.17";
        } else if (year == 20 && week >= 27 && week <= 30) {
            return "1.16.2";
        } else if (year == 20 && week >= 6 && week <= 22) {
            return "1.16";
        } else if (year == 19 && week >= 34) {
            return "1.15";
        } else if (year == 18 && week >= 43 || year == 19 && week <= 14) {
            return "1.14";
        } else if (year == 18 && week >= 30 && week <= 33) {
            return "1.13.1";
        } else if (year == 17 && week >= 43 || year == 18 && week <= 22) {
            return "1.13";
        } else if (year == 17 && week == 31) {
            return "1.12.1";
        } else if (year == 17 && week >= 6 && week <= 18) {
            return "1.12";
        } else if (year == 16 && week == 50) {
            return "1.11.1";
        } else if (year == 16 && week >= 32 && week <= 44) {
            return "1.11";
        } else if (year == 16 && week >= 20 && week <= 21) {
            return "1.10";
        } else if (year == 16 && week >= 14 && week <= 15) {
            return "1.9.3";
        } else if (year == 15 && week >= 31 || year == 16 && week <= 7) {
            return "1.9";
        } else if (year == 14 && week >= 2 && week <= 34) {
            return "1.8";
        } else if (year == 13 && week >= 47 && week <= 49) {
            return "1.7.3";
        } else if (year == 13 && week >= 36 && week <= 43) {
            return "1.7";
        } else if (year == 13 && week >= 16 && week <= 26) {
            return "1.6";
        } else if (year == 13 && week >= 11 && week <= 12) {
            return "1.5.1";
        } else if (year == 13 && week >= 1 && week <= 10) {
            return "1.5";
        } else if (year == 12 && week >= 49 && week <= 50) {
            return "1.4.6";
        } else if (year == 12 && week >= 32 && week <= 42) {
            return "1.4";
        } else if (year == 12 && week >= 15 && week <= 30) {
            return "1.3";
        } else if (year == 12 && week >= 3 && week <= 8) {
            return "1.2";
        } else if (year == 11 && week >= 47 || year == 12 && week <= 1) {
            return "1.1";
        }

        return null;
    }

    private static String normalizeSpecialVersion(String version) {
        return switch (version) {
            case "13w12~" ->
                // A pair of debug snapshots immediately before 1.5.1-pre
                    "1.5.1-alpha.13.12.a";
            case "22w13oneblockatatime" ->
                "1.18.2-alpha.22.13.oneblockatatime";
            case "15w14a" ->
                // The Love and Hugs Update, forked from 1.8.3
                    "1.8.4-alpha.15.14.a+loveandhugs";
            case "1.RV-Pre1" ->
                // The Trendy Update, probably forked from 1.9.2 (although the protocol/data versions immediately follow 1.9.1-pre3)
                    "1.9.2-rv+trendy";
            case "3D Shareware v1.34" ->
                // Minecraft 3D, forked from 19w13b
                    "1.14-alpha.19.13.shareware";
            case "20w14infinite" ->
                // The Ultimate Content update, forked from 20w13b
                    "1.16-alpha.20.13.inf"; // Not to be confused with the actual 20w14a
            case "1.14.3 - Combat Test" ->
                // The first Combat Test, forked from 1.14.3 Pre-Release 4
                    "1.14.3-rc.4.combat.1";
            case "Combat Test 2" ->
                // The second Combat Test, forked from 1.14.4
                    "1.14.5-combat.2";
            case "Combat Test 3" ->
                // The third Combat Test, forked from 1.14.4
                    "1.14.5-combat.3";
            case "Combat Test 4" ->
                // The fourth Combat Test, forked from 1.15 Pre-release 3
                    "1.15-rc.3.combat.4";
            case "Combat Test 5" ->
                // The fifth Combat Test, forked from 1.15.2 Pre-release 2
                    "1.15.2-rc.2.combat.5";
            case "Combat Test 6" ->
                // The sixth Combat Test, forked from 1.16.2 Pre-release 3
                    "1.16.2-beta.3.combat.6";
            case "Combat Test 7" ->
                // Private testing Combat Test 7, forked from 1.16.2
                    "1.16.3-combat.7";
            case "1.16_combat-2" ->
                // Private testing Combat Test 7b, forked from 1.16.2
                    "1.16.3-combat.7.b";
            case "1.16_combat-3" ->
                // The seventh Combat Test 7c, forked from 1.16.2
                    "1.16.3-combat.7.c";
            case "1.16_combat-4" ->
                // Private testing Combat Test 8(a?), forked from 1.16.2
                    "1.16.3-combat.8";
            case "1.16_combat-5" ->
                // The eighth Combat Test 8b, forked from 1.16.2
                    "1.16.3-combat.8.b";
            case "1.16_combat-6" ->
                // The ninth Combat Test 8c, forked from 1.16.2
                    "1.16.3-combat.8.c";
            case "2point0_red" ->
                // 2.0 update version red, forked from 1.5.1
                    "1.5.2-red";
            case "2point0_purple" ->
                // 2.0 update version purple, forked from 1.5.1
                    "1.5.2-purple";
            case "2point0_blue" ->
                // 2.0 update version blue, forked from 1.5.1
                    "1.5.2-blue";
            case "23w13a_or_b" ->
                // Minecraft 23w13a_or_b, forked from 23w13a
                    "1.20-alpha.23.13.ab";
            case "24w14potato" ->
                // Minecraft 24w14potato, forked from 24w12a
                    "1.20.5-alpha.24.12.potato";
            case "25w14craftmine" ->
                "1.20.5-alpha.25.14.craftmine";
            default -> null; //Don't recognise the version
        };
    }

}
