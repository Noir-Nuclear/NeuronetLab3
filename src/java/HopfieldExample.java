import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HopfieldExample {

    static List<List<Integer>> samples = List.of(
            List.of(
                    0, 0, 0, 1, 1, 0, 0, 0,
                    0, 0, 1, 0, 0, 1, 0, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    1, 1, 1, 1, 1, 1, 1, 1,
                    1, 0, 0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 1),
            List.of(
                    0, 0, 0, 1, 1, 0, 0, 0,
                    0, 0, 1, 0, 0, 1, 0, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    1, 1, 0, 0, 0, 0, 1, 1,
                    1, 0, 0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 1),
            List.of(
                    1, 0, 0, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1, 1, 1, 1,
                    1, 0, 0, 0, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 1,
                    1, 1, 1, 1, 1, 1, 1, 1)
    );

    static List<List<Integer>> inputs = List.of(
            List.of(
                    0, 0, 0, 1, 1, 0, 0, 1,
                    0, 0, 1, 0, 0, 1, 0, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 1, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    1, 1, 1, 1, 1, 1, 1, 1,
                    1, 0, 1, 1, 0, 0, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 1),
            List.of(
                    0, 0, 0, 1, 1, 0, 0, 0,
                    0, 0, 1, 0, 0, 1, 0, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 1, 0, 1, 0, 0, 1, 0,
                    1, 1, 0, 0, 0, 0, 1, 1,
                    1, 0, 0, 0, 1, 0, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 1));

    public static void main(String[] args) {
        HopfieldNetwork network = new HopfieldNetwork();
        network.learn(samples);
        network.getOutput(inputs.get(1).stream().map(Double::new).collect(Collectors.toList()));
    }

    static class HopfieldNetwork {
        List<List<Double>> weights;

        public void learn(List<List<Integer>> samples) {
            weights = new ArrayList<>();
            IntStream.range(0, samples.size()).forEach(i ->
                    weights = sumArrays(weights,
                            multipleArrays(
                                    samples.get(i).stream().
                                            map(value -> List.of(value == 1 ? 1.0 : -1.0)).
                                            collect(Collectors.toList()),
                                    transposeArray(
                                            samples.get(i).stream().
                                                    map(value -> List.of(value == 1 ? 1.0 : -1.0)).
                                                    collect(Collectors.toList())
                                    )
                            )
                    ));
            IntStream.range(0, weights.size()).forEach(i -> weights.get(i).set(i, 0.0));
            IntStream.range(0, weights.size()).forEach(i ->
                    IntStream.range(0, weights.get(i).size()).forEach(j ->
                            weights.get(i).set(j,
                                    weights.get(i).get(j) / weights.size()
                            )
                    )
            );
        }

        public List<Double> getOutput(List<Double> input) {
            System.out.println("Искаженный образ");
            showSample(input, 8);
            List<Double> output;
            do {
                output = new ArrayList<>(input);
                input = multipleArrays(weights,
                        output.stream().map(List::of).collect(Collectors.toList())).
                        stream().
                        map(row -> row.get(0)).
                        map(value -> value > 0.0 ? 1.0 : 0.0).
                        collect(Collectors.toList());
            } while (!isEqual(input, output));
            System.out.println("Распознанный образ");
            showSample(output, 8);
            return output;
        }
    }

    static void showSample(List<Double> input, int size) {
        IntStream.range(0, size).forEach(i -> {
            IntStream.range(0, size).forEach(j ->
                    System.out.print(input.get(i * size + j).intValue())
            );
            System.out.println("");
        });
    }

    static Boolean isEqual(List<Double> list1, List<Double> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        AtomicReference<Boolean> isEqual = new AtomicReference<>(true);
//        System.out.println("ТестНачало");
//        showSample(list1, 8);
//        System.out.println("ТестСередина");
//        showSample(list2, 8);
//        System.out.println("ТестКонец");
        IntStream.range(0, list1.size()).forEach(i ->
                isEqual.set(isEqual.get() && list1.get(i).compareTo(list2.get(i)) == 0)
        );
        return isEqual.get();
    }

    static List<List<Double>> sumArrays(List<List<Double>> list1, List<List<Double>> list2) {
        if (list1.size() == 0) {
            return new ArrayList<>(list2);
        }
        return IntStream.range(0, list1.size()).mapToObj(i ->
                IntStream.range(0, list1.get(i).size()).mapToObj(j ->
                        list1.get(i).get(j) + list2.get(i).get(j)).
                        collect(Collectors.toList())).
                collect(Collectors.toList());
    }

    static List<List<Double>> multipleArrays(List<List<Double>> list1, List<List<Double>> list2) {
        return list1.stream().map(row -> IntStream.range(0, list2.get(0).size()).mapToObj(j ->
                IntStream.range(0, row.size()).mapToDouble(k -> row.get(k) * list2.get(k).get(j)).sum()).
                collect(Collectors.toList())).
                collect(Collectors.toList());
    }

    static List<List<Double>> transposeArray(List<List<Double>> list) {
        return IntStream.range(0, list.get(0).size())
                .mapToObj(i -> list.stream().map(l -> l.get(i)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
