package uia.sample.samples;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.scene.Node;
import javafx.scene.control.Label;
import uia.sample.Sample;

import uia.sample.samples.vlist.VList;

public class VListSample implements Sample {



    @Override
    public String getName() {
        return "VList";
    }

    @Override
    public Node getDescription() {
        return new Label("List");
    }



    @Override
    public Node getSample() {
        List<String> data = IntStream.range(0, 40_000).mapToObj(i -> "Item "+ i).collect(Collectors.toList());
        VList<String> list = new VList<>(model -> model);
        list.getItems().addAll(data);
        return list;
    }

    @Override
    public Node getControls() {
        return null;
    }

}
