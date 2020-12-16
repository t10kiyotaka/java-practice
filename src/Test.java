class Test {
    public static void main(String[] args) {
        String[] a = { "a", "b" };
        String[] c = a.clone();
        c[0] = "c";

        System.out.println(a[0]);
        System.out.println(c[0]);

    }


}
