package org.example.measurement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class NDCG {


    public static double compute(
            List<Integer> ranked_items,
            Collection<Integer> correct_items,
            Collection<Integer> ignore_items) {

        if (ignore_items == null)
            ignore_items = new HashSet<Integer>();

        double dcg   = 0;
        double idcg  = computeIDCG(correct_items.size());
        int left_out = 0;

        for (int i = 0; i < ranked_items.size(); i++) {
            int item_id = ranked_items.get(i);
            if (ignore_items.contains(item_id)) {
                left_out++;
                continue;
            }

            if (!correct_items.contains(item_id))
                continue;

            // compute NDCG part
            int rank = i + 1 - left_out;
            dcg += Math.log(2) / Math.log(rank + 1);

        }

        return dcg / idcg;
    }

    /**
     * Computes the ideal DCG given the number of positive items..
     *
     * See http://recsyswiki.com/wiki/Discounted_Cumulative_Gain
     *
     * @return the ideal DCG
     * <param name='n'>the number of positive items
     */
    static double computeIDCG(int n)
    {
        double idcg = 0;
        for (int i = 0; i < n; i++)
            idcg += Math.log(2) / Math.log(i + 2);
        return idcg;
    }



























//    /**
//     * Compute the normalized discounted cumulative gain (NDCG) of a list of ranked items.
//     *
//     * @return the NDCG for the given data
//     */
//    public static double calculateNDCG(List<String> realData, List<String> predictionData) {
//        double dcg = 0;
//        double idcg = calculateIDCG(realData.size());
//
//        if (idcg == 0) {
//            return 0;
//        }
//
//        for (int i = 0; i < predictionData.size(); i++) {
//            String predictedItem = predictionData.get(i);
//
//            if (!realData.contains(predictedItem))
//                continue;
//
//            // the relevance in the DCG part is either 1 (the item is contained in real data)
//            // or 0 (item is not contained in the real data)
//            int itemRelevance = 1;
//            if (!realData.contains(predictedItem))
//                itemRelevance = 0;
//
//            // compute NDCG part
//            int rank = i + 1;
//
//            dcg += (Math.pow(2, itemRelevance) - 1.0) * (Math.log(2) / Math.log(rank + 1));
//        }
//
//        return dcg / idcg;
//    }
//
//    /**
//     * Calculates the iDCG
//     *
//     * @param n size of the expected resource list
//     * @return iDCG
//     */
//    public static double calculateIDCG(int n) {
//        double idcg = 0;
//        // if can get relevance for every item should replace the relevance score at this point, else
//        // every item in the ideal case has relevance of 1
//        int itemRelevance = 1;
//
//        for (int i = 0; i < n; i++){
//            idcg += (Math.pow(2, itemRelevance) - 1.0) * ( Math.log(2) / Math.log(i + 2) );
//        }
//
//        return idcg;
//    }

}