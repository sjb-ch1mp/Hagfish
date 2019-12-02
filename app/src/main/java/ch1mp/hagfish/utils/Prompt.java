package ch1mp.hagfish.utils;

public enum Prompt {

    ENTER_PASSWORD("ENTER YOUR PASSWORD."),
    WRONG_PASSWORD("INCORRECT.\n{remaining_attempts} ATTEMPTS REMAIN."),
    SET_UP("WELCOME TO HAGFISH.\nENTER A PASSWORD.");

    String message;

    Prompt(String message)
    {
        this.message = message;
    }

    public String getMessage(){ return message; }

}
