package org.example.measurement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Measurement {
    public static double computeNDCG(List<String> rankedItems, Collection<String> correctItems, Collection<String> ignoreItems) {
        if (ignoreItems == null)
            ignoreItems = new HashSet<>();

        double dcg  = 0;
        double idcg = computeIDCG(correctItems.size());
        int leftOut = 0;

        for (int i = 0; i < rankedItems.size(); i++) {
            String item_id = rankedItems.get(i);
            if (ignoreItems.contains(item_id)) {
                leftOut++;
                continue;
            }

            if (!correctItems.contains(item_id))
                continue;

            // compute NDCG part
            int rank = i + 1 - leftOut;
            dcg += Math.log(2) / Math.log(rank + 1);
        }

        return dcg / idcg;
    }

    static double computeIDCG(int n)
    {
        double idcg = 0;
        for (int i = 0; i < n; i++)
            idcg += Math.log(2) / Math.log(i + 2);
        return idcg;
    }

    public static double computeMRR(List<Integer> ranks) {
        if (ranks.isEmpty()) {
            throw new IllegalArgumentException("Input list of ranks is empty.");
        }
        return ranks.stream()
                .map(rank -> rank == 0 ? 0.0 : 1.0 / rank)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}