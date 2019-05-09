import com.bawer.tasks.revolut.ewallet.disruptor.TransferEvent
import com.bawer.tasks.revolut.ewallet.model.Account

typealias LongIdGenerator = () -> Long

typealias LongIdGeneratorFactory = () -> LongIdGenerator