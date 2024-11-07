package com.bignerdranch.android.client;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private static List<Polyline> lines = new ArrayList<>();
    private static boolean lifeStoryLines = true;
    private static boolean familyTreeLines = true;
    private static boolean spouseLines = true;

    public static List<Polyline> getLines() {
        return lines;
    }

    public static void setLines(List<Polyline> lines) {
        Line.lines = lines;
    }

    public static boolean getLifeStoryLines() {
        return lifeStoryLines;
    }

    public static void setLifeStoryLines(boolean lifeStoryLines) {
        Line.lifeStoryLines = lifeStoryLines;
    }

    public static boolean getFamilyTreeLines() {
        return familyTreeLines;
    }

    public static void setFamilyTreeLines(boolean familyTreeLines) {
        Line.familyTreeLines = familyTreeLines;
    }

    public static boolean getSpouseLines() {
        return spouseLines;
    }

    public static void setSpouseLines(boolean spouseLines) {
        Line.spouseLines = spouseLines;
    }
}
