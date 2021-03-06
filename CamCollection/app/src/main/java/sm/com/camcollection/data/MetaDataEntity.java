package sm.com.camcollection.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "pass_data_table_1")
public class MetaDataEntity implements Parcelable{

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

    @ColumnInfo(name = "frequency")
    private int frequency;

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
        this.frequency = 0;
    }

    public MetaDataEntity(Parcel in) {
        this.id = in.readInt();
        this.positionId = in.readInt();
        this.domain = in.readString();
        this.userName = in.readString();
        this.length = in.readInt();
        this.hasNumber = in.readInt();
        this.hasSymbols = in.readInt();
        this.hasLettersUp = in.readInt();
        this.hasLetterLow = in.readInt();
        this.pwVersion = in.readInt();
        this.frequency = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(positionId);
        dest.writeString(domain);
        dest.writeString(userName);
        dest.writeInt(length);
        dest.writeInt(hasNumber);
        dest.writeInt(hasSymbols);
        dest.writeInt(hasLettersUp);
        dest.writeInt(hasLetterLow);
        dest.writeInt(pwVersion);
        dest.writeInt(frequency);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MetaDataEntity createFromParcel(Parcel in) {
            return new MetaDataEntity(in);
        }

        public MetaDataEntity[] newArray(int size) {
            return new MetaDataEntity[size];
        }
    };

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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
