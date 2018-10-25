package sm.com.passwordwallet.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "pass_data_table")
public class MetaDataEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "positionId")
    private int positionId;

    @ColumnInfo(name = "domain")
    private String domain;

    @ColumnInfo(name = "userName")
    private String userName;

    @ColumnInfo(name = "length")
    private int length;

    @ColumnInfo(name = "hasNumber")
    private int hasNumber;

    @ColumnInfo(name = "hasSymbols")
    private int hasSymbols;

    @ColumnInfo(name = "hasLettersUp")
    private int hasLettersUp;

    @ColumnInfo(name = "hasLetterLow")
    private int hasLetterLow;

    @ColumnInfo(name = "pwVersion")
    private int pwVersion;

    public MetaDataEntity(@NonNull int id, int positionId, String domain, String userName, int length, int hasNumber, int hasSymbols, int hasLettersUp, int hasLetterLow, int pwVersion) {
        this.id = id;
        this.positionId = positionId;
        this.domain = domain;
        this.userName = userName;
        this.length = length;
        this.hasNumber = hasNumber;
        this.hasSymbols = hasSymbols;
        this.hasLettersUp = hasLettersUp;
        this.hasLetterLow = hasLetterLow;
        this.pwVersion = pwVersion;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHasNumber() {
        return hasNumber;
    }

    public void setHasNumber(int hasNumber) {
        this.hasNumber = hasNumber;
    }

    public int getHasSymbols() {
        return hasSymbols;
    }

    public void setHasSymbols(int hasSymbols) {
        this.hasSymbols = hasSymbols;
    }

    public int getHasLettersUp() {
        return hasLettersUp;
    }

    public void setHasLettersUp(int hasLettersUp) {
        this.hasLettersUp = hasLettersUp;
    }

    public int getHasLetterLow() {
        return hasLetterLow;
    }

    public void setHasLetterLow(int hasLetterLow) {
        this.hasLetterLow = hasLetterLow;
    }

    public int getPwVersion() {
        return pwVersion;
    }

    public void setPwVersion(int pwVersion) {
        this.pwVersion = pwVersion;
    }
}
