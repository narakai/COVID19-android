package clem.app.mymvvm.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CovidItem {
    @Id(autoincrement = true)
    private Long id;
    private int type;
    private String province;
    private String country;
    private String lat;
    private String lng;
    //1/22/20-0
    @Convert(converter = DailyJsonBeanConvert.class , columnType = String.class)
    private List<ItemsBean> dailyItems;

    @Generated(hash = 1764321335)
    public CovidItem(Long id, int type, String province, String country, String lat, String lng,
            List<ItemsBean> dailyItems) {
        this.id = id;
        this.type = type;
        this.province = province;
        this.country = country;
        this.lat = lat;
        this.lng = lng;
        this.dailyItems = dailyItems;
    }

    @Generated(hash = 1621346819)
    public CovidItem() {
    }

    public static class DailyJsonBeanConvert implements PropertyConverter<List<ItemsBean>, String> {

        @Override
        public List<ItemsBean> convertToEntityProperty(String databaseValue) {
            return new Gson().fromJson(databaseValue, new TypeToken<List<ItemsBean>>() { }.getType());
        }

        @Override
        public String convertToDatabaseValue(List<ItemsBean> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }

    public static class ItemsBean implements Parcelable {
        private String date;
        private int number;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.date);
            dest.writeInt(this.number);
        }

        public ItemsBean() {
        }

        protected ItemsBean(Parcel in) {
            this.date = in.readString();
            this.number = in.readInt();
        }

        public static final Parcelable.Creator<ItemsBean> CREATOR = new Parcelable.Creator<ItemsBean>() {
            @Override
            public ItemsBean createFromParcel(Parcel source) {
                return new ItemsBean(source);
            }

            @Override
            public ItemsBean[] newArray(int size) {
                return new ItemsBean[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemsBean itemsBean = (ItemsBean) o;

            return date.equals(itemsBean.date);
        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovidItem covidItem = (CovidItem) o;

        if (province != null ? !province.equals(covidItem.province) : covidItem.province != null)
            return false;
        return country.equals(covidItem.country);
    }

    @Override
    public int hashCode() {
        int result = province != null ? province.hashCode() : 0;
        result = 31 * result + country.hashCode();
        return result;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public List<ItemsBean> getDailyItems() {
        return this.dailyItems;
    }

    public void setDailyItems(List<ItemsBean> dailyItems) {
        this.dailyItems = dailyItems;
    }
}
