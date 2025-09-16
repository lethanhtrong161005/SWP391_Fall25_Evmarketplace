package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums;

import java.util.HashMap;
import java.util.Map;

public enum MobileNetwork {
    VIETTEL(new String[]{"032","033","034","035","036","037","038","039","086","096","097","098"}),
    MOBIFONE(new String[]{"070","076","077","078","079","089","090","093"}),
    VINAPHONE(new String[]{"081","082","083","084","085","088","091","094"}),
    VIETNAMOBILE(new String[]{"052","056","058","092"}),
    GMOBILE(new String[]{"059","099"}),
    ITEL(new String[]{"087"});

    private final String[] prefixes;
    private static final Map<String, MobileNetwork> prefixMap = new HashMap<>();

    static {
        for (MobileNetwork network : MobileNetwork.values()) {
            for (String prefix : network.prefixes) {
                prefixMap.put(prefix, network);
            }
        }
    }

    MobileNetwork(String[] prefixes) {
        this.prefixes = prefixes;
    }

    public static MobileNetwork fromPhoneNumber(String phone) {
        if (phone == null || phone.length() < 3) return null;
        String prefix = phone.substring(0, 3);
        return prefixMap.get(prefix);
    }

}
