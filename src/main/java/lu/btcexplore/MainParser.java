package lu.btcexplore;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainParser {

    public static void main(String[] args) {
// Arm the blockchain file loader.
        NetworkParameters np = new MainNetParams();
        List<File> blockChainFiles = new ArrayList<>();
        //blockChainFiles.add(new File("E:\\bitcoin\\blocks\\blk00000.dat"));

        File path = new File("E:\\bitcoin\\blocks\\");

        File [] files = path.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile() && file.getName().contains("blk")) {
                blockChainFiles.add(file);
            }
        }


        BlockFileLoader bfl = new BlockFileLoader(np, blockChainFiles);

        org.apache.log4j.BasicConfigurator.configure();
        Context c = Context.getOrCreate(np);


// Data structures to keep the statistics.
        Map<String, Integer> monthlyTxCount = new HashMap<>();
        Map<String, Integer> monthlyBlockCount = new HashMap<>();

        long start=System.currentTimeMillis();
        long counter=0;
// Iterate over the blocks in the dataset.
        for (Block block : bfl) {

            // Extract the month keyword.
            String month = new SimpleDateFormat("yyyy-MM").format(block.getTime());

            // Make sure there exists an entry for the extracted month.
            if (!monthlyBlockCount.containsKey(month)) {
                monthlyBlockCount.put(month, 0);
                monthlyTxCount.put(month, 0);
            }

            // Update the statistics.
            monthlyBlockCount.put(month, 1 + monthlyBlockCount.get(month));
            monthlyTxCount.put(month, block.getTransactions().size() + monthlyTxCount.get(month));
            counter++;
            if(counter%10000==0){
                long endt=System.currentTimeMillis();
                double tt=endt-start;
                tt=tt/1000;
                System.out.println("Loaded block: "+counter+" in "+tt+"s");
            }

        }

// Compute the average number of transactions per block per month.
        Map<String, Float> monthlyAvgTxCountPerBlock = new HashMap<>();
        for (String month : monthlyBlockCount.keySet()) {
            float res = (float) monthlyTxCount.get(month) / monthlyBlockCount.get(month);
            monthlyAvgTxCountPerBlock.put(month, res);
            System.out.println("month: "+month+" avg: "+res);
        }
    }
}
