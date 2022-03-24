import java.io.Serializable;

public class ClientInput implements Serializable {

    private String action ;
    private float value;

    public String getAction() {
        return action;
    }

    public float getValue() {
        return value;
    }

    public ClientInput(String action) {
        this.action = action;
    }

    public ClientInput(String action, float value) {
        this.action = action;
        this.value = value;
    }
}
