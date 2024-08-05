package ui;

import java.util.List;

import model.AuthData;

public class PostloginUI extends UserInterface {
    PostloginUI() {
        super();
        this.cmds.put("logout",
                new FunctionPair<>(List.of("logout", "signout", "exit"), "Sign out of your account.", this::logout));
    }

    private String logout() {
        Data.getInstance().getServerFacade()
                .logout(new AuthData(Data.getInstance().getAuthToken(), Data.getInstance().getUsername()));
        return EscapeSequences.SET_TEXT_COLOR_RED + "Successfully logged out." + EscapeSequences.RESET_TEXT_COLOR;
    }
}
