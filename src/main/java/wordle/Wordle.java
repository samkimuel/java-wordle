package wordle;

import java.time.LocalDate;
import java.util.List;

public class Wordle {

    private static final LocalDate CUTOFF_DATE = LocalDate.of(2021, 6, 19);
    private static final int TRY_COUNT_LIMIT = 6;

    private final IOView ioView;
    private final WordsReader wordsReader;
    private final WordleValidator wordleValidator;
    private final TileService tileService;

    public Wordle(IOView ioView, WordsReader wordsReader, WordleValidator wordleValidator, TileService tileService) {
        this.ioView = ioView;
        this.wordsReader = wordsReader;
        this.wordleValidator = wordleValidator;
        this.tileService = tileService;
    }

    public void start() {
        ioView.printInitGameMessage();

        List<String> wordList = wordsReader.read();
        Words words = new Words(wordList, CUTOFF_DATE);
        LocalDate now = LocalDate.now();
//        String wordOfDay = words.getWordOfDay(now);
        String wordOfDay = "apple";

        Letters answerLetters = new Letters(wordOfDay);

        int tryCount = 0;
        while (tryCount < TRY_COUNT_LIMIT) {
            tryCount++;

            ioView.printInputAnswerMessage();
            String input = ioView.inputAnswer();

            Letters inputLetters = new Letters(input);
            if (wordleValidator.isInvalidLength(inputLetters)) {
                ioView.printNotEnoughLettersMessage();
                continue;
            }

            if (wordleValidator.isNotIncludedWord(inputLetters, words)) {
                ioView.printNotInWordListMessage();
                continue;
            }

            Tiles tiles = tileService.create(answerLetters, inputLetters);

            // 정답이면 탈출, 6번 초과 실패
            if (tileService.isAnswer(tiles)) {
                // 정답 여부만 체크
                ioView.printTryCount(tryCount, TRY_COUNT_LIMIT);
                ioView.printTiles(tileService.findAll());
                break;
            }

            // 6번째 시도 시 틀렸을 때
            if (tryCount == TRY_COUNT_LIMIT) {
                ioView.printTryCount("X", TRY_COUNT_LIMIT);
                ioView.printTiles(tileService.findAll());
                break;
            }

            ioView.printTiles(tileService.findAll());
        }
    }
}
