public class RemoteUtilTest {
    private static final Properties props = new Properties();

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "dc02-kafka01:9091");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dist-consumer-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // config for MM2 (bootstrap.servers in destination side is required)
        Map<String, Object> mmConfig = new HashMap<>();
        mmConfig.put("bootstrap.servers", "dc02-kafka01:9091");

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // get synced consumer offset
            Map<TopicPartition, OffsetAndMetadata> destinationOffsetsMap = RemoteClusterUtils.translateOffsets(mmConfig, "kafka-source", "source-consumer-group", Duration.ofMillis(10000));
            destinationOffsetsMap.forEach(((topicPartition, offsetAndMetadata) -> System.out.printf("topicPartiion: %s, offsetAndMetadata: %s%n", topicPartition, offsetAndMetadata) ));

            // set the offset
            List<TopicPartition> topicPartitions = destinationOffsetsMap.keySet().stream().collect(Collectors.toList());
            destinationOffsetsMap.forEach(((topicPartition, offsetAndMetadata) -> consumer.assign(topicPartitions)));
            destinationOffsetsMap.forEach(((topicPartition, offsetAndMetadata) -> consumer.seek(topicPartition, offsetAndMetadata)));
            while (true) {
                final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (final ConsumerRecord<String, String> record : records) {
                    final String key = record.key();
                    final String value = record.value();
                    System.out.printf("key = %s, value = %s%n", key, value);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();