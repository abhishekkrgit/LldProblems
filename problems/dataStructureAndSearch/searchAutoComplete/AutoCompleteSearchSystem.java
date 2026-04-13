package dataStructureAndSearch.searchAutoComplete;

import java.util.*;

class Suggestion {

    private final String word;
    private int weight;

    public Suggestion(String word, int weight) {
        this.word = word;
        this.weight = weight;
    }

    public String getWord() {
        return this.word;
    }

    public int getWeight() {
        return this.weight;
    }

    public void incrementWeight() {
        weight++;
    }

}

interface RankingStrategy {

    void sort(List<Suggestion> suggestions);
}

class FreqBasedRanking implements RankingStrategy {

    @Override
    public void sort(List<Suggestion> suggestions) {
        suggestions.sort((a, b) -> {
            if (b.getWeight() != a.getWeight()) {
                return b.getWeight() - a.getWeight();
            }
            return a.getWord().compareTo(b.getWord());
        });
    }

}

class AlphabeticalRanking implements RankingStrategy {

    @Override
    public void sort(List<Suggestion> suggestions) {
        suggestions.sort((a, b) -> {
            return a.getWord().compareTo(b.getWord());
        });
    }
}

class Node {

    Map<Character, Node> children;
    List<Suggestion> suggestionList = new ArrayList<>();
    boolean isEndOfWord;
    int freq;

    public Node() {
        children = new HashMap<>();
        isEndOfWord = false;
        freq = 0;
    }

    public Node getNode(Character ch) {
        if (children.containsKey(ch)) {
            return children.get(ch);
        }
        return null;
    }

    public int getFreq() {
        return freq;
    }

    public void setEndOfWord(boolean isEnd) {
        isEndOfWord = isEnd;
    }

    public void incrementFreq() {
        freq++;
    }

    public Node insertKey(Character ch) {
        if (children.containsKey(ch)) {
            return children.get(ch);
        }
        Node node = new Node();
        children.put(ch, node);
        return node;
    }

    public List<String> getSuggestion() {
        List<String> suggestionRes = new ArrayList<>();
        for (Suggestion currSuggestion : suggestionList) {
            suggestionRes.add(currSuggestion.getWord());
        }

        return suggestionRes;
    }

    public void updateSuggestion(String word, int freq, int resultLimit, RankingStrategy strategy) {
        Suggestion newSuggestion = new Suggestion(word, freq);
        suggestionList.removeIf((s) -> {
            return s.getWord().equals(newSuggestion.getWord());
        });
        suggestionList.add(newSuggestion);
        strategy.sort(suggestionList);
        if (suggestionList.size() > resultLimit) {
            suggestionList.remove(suggestionList.size() - 1);
        }
    }

}

class Trie {

    private final Node root;
    private final int resultLimit;
    private RankingStrategy rankingStrategy;

    public Trie(RankingStrategy rankingStrategy, int resultLimit) {
        root = new Node();
        this.rankingStrategy = rankingStrategy;
        this.resultLimit = resultLimit;
    }

    public void setRankingStrategy(RankingStrategy strategy) {
        this.rankingStrategy = strategy;
    }

    public void insertWord(String word) {
        Node currNode = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            currNode.insertKey(ch);
            currNode = currNode.getNode(ch);
        }
        currNode.incrementFreq();
        currNode.setEndOfWord(true);
        int freq = currNode.getFreq();
        updateFreq(word, freq);
    }

    private void updateFreq(String word, int freq) {
        Node currNode = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            currNode = currNode.getNode(ch);
            currNode.updateSuggestion(word, freq, resultLimit, rankingStrategy);
        }
    }

    public List<String> getSuggestions(String prefix) {
        Node currNode = root;
        for (char ch : prefix.toLowerCase().toCharArray()) {
            currNode = currNode.getNode(ch);
            if (Objects.isNull(currNode)) {
                return new ArrayList<>();
            }
        }

        return currNode.getSuggestion();
    }

}

public class AutoCompleteSearchSystem {

    public static void main(String[] args) {
        Trie autocomplete = new Trie(new FreqBasedRanking(), 3);
        // Requirement 6: Dynamic addition
        autocomplete.insertWord("apple");
        autocomplete.insertWord("apple");
        autocomplete.insertWord("apply");
        autocomplete.insertWord("appla");
        autocomplete.insertWord("app");
        autocomplete.insertWord("banana");

        // Case-insensitive search
        System.out.println("Suggestions for 'AP': " + autocomplete.getSuggestions("AP"));
        // Switch Strategy to Alphabetical
        autocomplete.setRankingStrategy(new AlphabeticalRanking());

        // Note: For Alphabetical to reflect immediately in pre-computed lists, 
        // you would normally re-insert or have a re-sort trigger. 
        // With your current code, the NEXT insertion will re-sort the path alphabetically.
        autocomplete.insertWord("apartment");
        System.out.println("Suggestions for 'ap' (Alpha): " + autocomplete.getSuggestions("ap"));

    }
}
