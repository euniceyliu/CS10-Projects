/**
 * @author May Oo Khine, You-chi Liu, CS10, W22
 *
 * Create class to store character and frequency
 */


public class CharNFreq {
    private Character character;
    private Integer frequency;

    public CharNFreq(Character character, Integer frequency) {
        this.character = character;

        this.frequency = frequency;
    }

    public Character getCharacter() {
        return character;
    }

    public Integer getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "Character: " + getCharacter() + ", Frequency: " + getFrequency();
    }
}