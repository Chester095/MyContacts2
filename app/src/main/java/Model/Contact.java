package Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contact_id")
    private long id;

    @ColumnInfo(name = "contact_name")
    private String name;

    @ColumnInfo(name = "contact_family")
    private String family;

    @ColumnInfo(name = "contact_email")
    private String email;

    @ColumnInfo(name = "contact_phone")
    private String phone;

    @Ignore
    public Contact() {
    }

    public Contact(long id, String name, String family, String email, String phone) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.email = email;
        this.phone = phone;
    }

    @Ignore
    public Contact(String name, String family, String email, String phone) {
        this.name = name;
        this.family = family;
        this.email = email;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }
    public void setFamily(String family) {
        this.family = family;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }


}
