# Spam validation for Java (sv4j)
 
* Project is based on [CnnSentenceClassification from DL4J examples](https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/CnnSentenceClassificationExample.java)
* Trained on 8k messages with 5k epochs

### Installing
Jar: [link](https://github.com/BadlyDrunkScotsman/SV4J/releases/tag/1.0.0)

### Examples
```
public class MessageValidatorTest {

    private MessageValidator mv = new MessageValidator();

    public static void main(String... args){
        //Simple validation
        System.out.println(mv.isSpam("We would like to offer cheapest Viagra in the world!"));
        System.out.println(mv.isSpam("We would like to offer you an internship program for students. We have the pleasure of having many of you from Universities from all over the world as our interns. The Little Match Girl project has been in operation for 16 years. Our main idea is to raise funds for charity purposes (to help children living in poverty) by promoting the sale of Fairy Matches. This may take different forms: from simple activities where children pack the shopping for customers at a supermarket, and offer matches to them, to major events like shows, performances, spring-break and summer vacations. It is an excellent opportunity to gain and improve your experience in working with children from elementary and high schools. Our internship program is intended mainly for students of management, marketing, sociology, pedagogy, law, administration, and physical education university departments as well as for those who are planning to work with children in the future.").isSpam());
        System.out.println(mv.isSpam("This is the 2nd time we have tried 2 contact u. U have won the £750 Pound prize. 2 claim is easy, call 087187272008 NOW1!").isSpam());

        //Detail validation
        NeuralNetOutput nno = mv.getOutputFor("We would like to offer cheapest Viagra in the world!");
        nno.printValues()
    }
}
```

## Training resources

* [enron collection](https://www.cs.cmu.edu/~./enron/)
* [sms collection](https://archive.ics.uci.edu/ml/datasets/sms+spam+collection)


## Built With

* [DL4J](https://github.com/deeplearning4j/)
* [ND4J](https://github.com/deeplearning4j/nd4j)
* [SLF4J](https://www.slf4j.org/)


## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
