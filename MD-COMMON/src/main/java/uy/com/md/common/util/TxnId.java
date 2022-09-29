package uy.com.md.common.util;

/**
 * La dependencia org.jpos.ee.jposee-txn de jpos tiene problemas d ecom patibilidad  con OpenAPI,
 * por lo que se copia solo esta clase del ccodigo fuente de la misma
 */


import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxnId {
    private long id;
    private static final long YMUL = 10000000000000000L;
    private static final long DMUL = 10000000000000L;
    private static final long SMUL = 100000000L;
    private static final long NMUL = 100000L;
    private static final long MAX_VALUE = Long.parseLong("zzzzzzzzzzzz", 36);
    private static Pattern pattern = Pattern.compile("^([\\d]{3})-([\\d]{3})-([\\d]{5})-([\\d]{3})-([\\d]{5})$");

    private TxnId() {
    }

    private TxnId(long l) {
        this.id = l;
    }

    public long id() {
        return this.id;
    }

    private TxnId init(int year, int dayOfYear, int secondOfDay, int node, long transactionId) {
        this.id = (long) year * 10000000000000000L + (long) dayOfYear * 10000000000000L + (long) secondOfDay * 100000000L + (long) (node % 1000) * 100000L + transactionId % 100000L;
        return this;
    }

    public String toString() {
        long l = this.id;
        int yy = (int) (this.id / 10000000000000000L);
        l -= (long) yy * 10000000000000000L;
        int dd = (int) (l / 10000000000000L);
        l -= (long) dd * 10000000000000L;
        int ss = (int) (l / 100000000L);
        l -= (long) ss * 100000000L;
        int node = (int) (l / 100000L);
        l -= (long) node * 100000L;
        return String.format("%03d-%03d-%05d-%03d-%05d", yy, dd, ss, node, l);
    }

    public String toRrn() {
        return Long.toString(this.id, 36);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TxnId tranLogId = (TxnId) o;
            return this.id == tranLogId.id;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id});
    }

    public static TxnId create(LocalDateTime dt, int node, long transactionId) {
        TxnId id = new TxnId();
        return id.init(dt.getYear() - 2000, dt.getDayOfYear(), dt.getNano(), node, transactionId);
    }

    public static TxnId parse(String idString) {
        Matcher matcher = pattern.matcher(idString);
        if (!matcher.matches() && matcher.groupCount() != 5) {
            throw new IllegalArgumentException("Invalid idString '" + idString + "'");
        } else {
            return (new TxnId()).init(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)));
        }
    }

    public static TxnId parse(long id) {
        if (id >= 0L && id <= MAX_VALUE) {
            return new TxnId(id);
        } else {
            throw new IllegalArgumentException("Invalid id " + id);
        }
    }

    public static TxnId fromRrn(String rrn) {
        long id = Long.parseLong(rrn, 36);
        if (id >= 0L && id <= MAX_VALUE) {
            return new TxnId(id);
        } else {
            throw new IllegalArgumentException("Invalid rrn " + rrn);
        }
    }
}

