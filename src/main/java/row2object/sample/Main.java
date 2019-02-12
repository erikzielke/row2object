package row2object.sample;

import static row2object.Row2Object.rowParser;

import java.io.File;
import java.util.List;
import row2object.ProgressHandler;
import row2object.Result;
import row2object.errors.ValueError;

public class Main {
    public static void main(String[] args) {

        ProgressHandler progressHandler = (progressState, currentIndex, totalRows) -> System.out.println("Handling " + currentIndex + " of " + totalRows);


//        rowParser(Person.class)
//                .structureErrorHandler(errors -> errors.forEach(System.out::println))
//                .invalidRowHandler(Main::writeRowError)
//                .progressHandler(progressHandler)
//                .rowHandler(row -> System.out.println("row.getData() = " + row.getData()))
//                .parse(new File("C:\\row2object\\src\\main\\java\\row2object\\sample\\test.csv"));

        Result<Person> result = rowParser(Person.class)
                .progressHandler(progressHandler)
                .parseResult(new File("C:\\row2object\\src\\main\\java\\row2object\\sample\\test.csv"));

    }

    private static void writeRowError(long index, List<ValueError> errors) {
        System.out.println("index = " + index);
        for (ValueError error : errors) {
            System.out.println("error = " + error);
        }
    }

}
