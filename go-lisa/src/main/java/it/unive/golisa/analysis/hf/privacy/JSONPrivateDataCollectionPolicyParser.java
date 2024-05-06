package it.unive.golisa.analysis.hf.privacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONPrivateDataCollectionPolicyParser {

    public static List<PrivateDataPolicy> parsePolicies(String filepath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Read JSON file
            File file = new File(filepath);
            JsonNode jsonNode = mapper.readTree(file);

            // Check if it's an array
            if (jsonNode.isArray()) {
                CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, PrivateDataPolicy.class);
                List<PrivateDataPolicy> collections = mapper.readValue(jsonNode.traverse(), collectionType);
                return collections;
            } else {
                System.out.println("JSON is not an array.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return new ArrayList<>();
    }

    public class PrivateDataPolicy {
        String name;
        String policy;
        int requiredPeerCount;
        int maxPeerCount;
        int blockToLive;
        boolean memberOnlyRead;
        boolean memberOnlyWrite;
        EndorsementPolicy endorsementPolicy;
    }

    static class EndorsementPolicy {
        String signaturePolicy;
        String channelConfigPolicy;
    }
   
}