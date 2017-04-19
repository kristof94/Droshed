package kristof.fr.droshed;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kristof
 * on 4/13/17.
 */

public class CustomItem implements Parcelable {

    private String text;

    public CustomItem(String text){
        this.text = text;
    }

    protected CustomItem(Parcel in) {
        text = in.readString();
    }

    public static final Creator<CustomItem> CREATOR = new Creator<CustomItem>() {
        @Override
        public CustomItem createFromParcel(Parcel in) {
            return new CustomItem(in);
        }

        @Override
        public CustomItem[] newArray(int size) {
            return new CustomItem[size];
        }
    };

    public String getText(){
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
    }
}
