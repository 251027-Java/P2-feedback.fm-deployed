package feedbackmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name = "listener")
public class Listener {

    @Id
    @Column(name = "listener_id", length = 64)
    private String listenerId;

    @Column(name = "display_name", columnDefinition = "TEXT")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String email;

    @Column(length = 10)
    private String country;
    
    @Column(columnDefinition = "TEXT")
    private String href;


    /* @OneToMany(mappedBy = "owner")
    private Set<Feedback> feedback = new HashSet<>(); 
    
    @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL)
    private Set<History> history = new HashSet<>();
    */

    public Listener() {}
    public Listener(String listenerId, String displayName, String email, String country, String href) {
        this.listenerId = listenerId;
        this.displayName = displayName;
        this.email = email;
        this.country = country;
        this.href = href;
    }

    public String getListenerId() {
        return listenerId;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Listener{" +
                "listenerId='" + listenerId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

}