package org.dataProcessor.importData;

import org.dataProcessor.model.Starship;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.Map;

// check if item vas already processed
public class CustomItemProcessor implements ItemProcessor<Starship, Starship> {

    public static Map<Integer, Starship> CACHE = new HashMap<>();

    @Override
    public Starship process(Starship item) {
        int hash = item.hashCode();
        if(!CACHE.containsKey(hash)) {
            item.setId(null);
            CustomItemProcessor.CACHE.put(hash, item);
            return item;
        }
        return null;
    }
}
