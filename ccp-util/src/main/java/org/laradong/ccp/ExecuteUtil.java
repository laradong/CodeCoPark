    //添加每批最多1k条记录的保护
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private RestTemplate restClient;

    private <T> void batchExchange(List<T> records, Consumer<List<T>> consumer) {
        int i = 0;
        int j = 0;
        while (i < records.size()) {
            j = i + BATCH_SIZE;
            if (j > records.size()) {
                j = records.size();
            }
            List<T> batch = records.subList(i, j);
            consumer.accept(batch);
            i += BATCH_SIZE;
        }
        if (j < records.size()) {
            consumer.accept(records.subList(j, records.size()));
        }
    }package org.laradong.ccp;
    
    public class ExecuteUtil {
        
    }