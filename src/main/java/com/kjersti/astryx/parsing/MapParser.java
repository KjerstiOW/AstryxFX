//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing;

import com.kjersti.astryx.parsing.object.OverwatchMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapParser {
    private List<OverwatchMap> overwatchMaps;

    public MapParser(List<OverwatchMap> overwatchMaps) {
        this.overwatchMaps = overwatchMaps;
    }

    public List<OverwatchMap> parseMaps() {
        this.setMapMetadata();
        if (!this.verifyMaps()) {
            return null;
        } else {
            this.addImplicitSubmaps();
            this.addSubmapsToList();
            return this.overwatchMaps;
        }
    }

    public void addSubmapsToList() {
        List<OverwatchMap> listWithSubmaps = new ArrayList();
        Iterator var2 = this.overwatchMaps.iterator();

        while(var2.hasNext()) {
            OverwatchMap map = (OverwatchMap)var2.next();
            listWithSubmaps.add(map);
            listWithSubmaps.addAll(map.getSubmaps());
        }

        this.overwatchMaps = listWithSubmaps;
    }

    public void addImplicitSubmaps() {
        this.overwatchMaps.forEach(OverwatchMap::addImplicitSubmaps);
    }

    public void setMapMetadata() {
        this.overwatchMaps.forEach(OverwatchMap::setMetadata);
    }

    public boolean verifyMaps() {
        return this.overwatchMaps.stream().allMatch(OverwatchMap::verifyMap);
    }

    public OverwatchMap findFirstInvalidMap() {
        Iterator<OverwatchMap> var1 = this.overwatchMaps.iterator();

        OverwatchMap map;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            map = (OverwatchMap)var1.next();
        } while(map.verifyMap());

        return map;
    }
}
