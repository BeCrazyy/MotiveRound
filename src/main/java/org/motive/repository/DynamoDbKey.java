package org.motive.repository;

public class DynamoDbKey {
    private final Object partitionKey;
    private final Object sortKey;

    public DynamoDbKey(Object partitionKey) {
        this.partitionKey = partitionKey;
        this.sortKey = null;
    }

    public DynamoDbKey(Object partitionKey, Object sortKey) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
    }

    public Object getPartitionKey() {
        return partitionKey;
    }

    public Object getSortKey() {
        return sortKey;
    }

    public boolean hasSortKey() {
        return sortKey != null;
    }

    @Override
    public String toString() {
        if (hasSortKey()) {
            return "DynamoDbKey{partitionKey=" + partitionKey + ", sortKey=" + sortKey + "}";
        } else {
            return "DynamoDbKey{partitionKey=" + partitionKey + "}";
        }
    }
}