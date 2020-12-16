import java.util.*;

class Main {
    public static void main(String[] args) {
        // put your code here
        Deque<String> stack = new ArrayDeque();
        Scanner sc = new Scanner(System.in);
        String[] input = sc.next().split("");

        Map<String, String> map = new HashMap<>();
        map.put("]", "[");
        map.put("}", "{");
        map.put(")", "(");

        for(String s : input) {
            if(s.matches("[(\\[{]")) {
                stack.offerLast(s);
            } else {
                if(map.get(s).equals(stack.peekLast())) {
                    stack.pollLast();
                } else {
                    stack.offerLast(s);
                }
            }
        }
        System.out.println(stack.isEmpty());
    }
}
