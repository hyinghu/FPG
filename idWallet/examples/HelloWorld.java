class HelloWorld {
    private static native String hello(String input, String time);

    static {
        System.out.println(System.getProperty("java.library.path"));


        //System.load("C:/netapp/test/workspace/fiMultiPartyECDSA/target/release/examples/gg18.dll");

        System.loadLibrary("gg18");
    }

    public static void main(String[] args) {


        //String output = HelloWorld.hello(" FI ", "10:30AM");
        String output = HelloWorld.hello(args[0], "10:30AM");
        System.out.println("\n\n" + output);
    }
}
