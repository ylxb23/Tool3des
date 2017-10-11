public enum OperatorEnum {
    ENCRYPT(0, "加密"),
    DECODE(1, "解密"),;

    private int code;
    private String name;

    OperatorEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static OperatorEnum valueOfName(String s) {
        OperatorEnum[] list = values();
        for (OperatorEnum item : list) {
            if (item.getName().equalsIgnoreCase(s)) {
                return item;
            }
        }
        throw new IllegalArgumentException("[" + s + "]不存在");
    }
}