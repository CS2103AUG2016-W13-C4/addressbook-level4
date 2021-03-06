package guitests;

import org.junit.Test;

//@@author A0093960X
public class InputHistoryTest extends DearJimGuiTest {

    @Test
    public void getPrevAndNextInput_singlePrevInput_ableToNavigatePrevAndNext() {
        commandBox.runCommand("list done");
        assertCommandInput("");
        assertGetPrevInputSuccess("list done");
        assertGetNextInputSuccess("");
    }

    @Test
    public void getPrevInput_prevInputWhileTypingHalfwayOnLatestInput_incompleteLatestInputSaved() {
        commandBox.runCommand("asdfasdfsdSDFSDFSDFSDFfind lol");
        commandBox.runCommand("fasdfsafsafas");
        commandBox.enterCommand("I hope this command is saved when I press up!!");
        assertGetPrevInputSuccess("fasdfsafsafas");
        assertGetPrevInputSuccess("asdfasdfsdSDFSDFSDFSDFfind lol");
        assertGetNextInputSuccess("fasdfsafsafas");
        assertGetNextInputSuccess("I hope this command is saved when I press up!!");
    }

    @Test
    public void getPrevInput_typingOverAPrevInput_savedPrevInputIsNotOverwritten() {
        commandBox.runCommand("list done");
        commandBox.runCommand("find lol");
        assertGetPrevInputSuccess("find lol");
        assertGetPrevInputSuccess("list done");
        commandBox.runCommand("This input will not overwrite the previous one that was here");
        assertCommandInput("");
        assertGetPrevInputSuccess("This input will not overwrite the previous one that was here");
        assertGetPrevInputSuccess("find lol");
        assertGetPrevInputSuccess("list done");
    }

    @Test
    public void getPrevAndNextInput_noPrevAndNextInput_noChange() {
        assertGetPrevInputSuccess("");
        assertGetNextInputSuccess("");
    }

    /**
     * Presses the up arrow key to get the previous input, and asserts that the
     * input displayed in the commandBox is the same is the expected String.
     * 
     * @param expected The expected String
     */
    private void assertGetPrevInputSuccess(String expected) {
        commandBox.getPreviousInput();
        assertCommandInput(expected);
    }

    /**
     * Presses the down arrow key to get the next input, and asserts that the
     * input displayed in the commandBox is the same is the expected String.
     * 
     * @param expected The expected String
     */
    private void assertGetNextInputSuccess(String expected) {
        commandBox.getNextInput();
        assertCommandInput(expected);
    }

}
