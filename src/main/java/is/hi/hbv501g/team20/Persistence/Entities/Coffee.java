package is.hi.hbv501g.team20.Persistence.Entities;

import jakarta.persistence.*;

@Entity
public class Coffee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "activity_id")
        private StudyActivity activity;
        //@Temporal(TemporalType.DATE)
       // private Date date;

        public Coffee(User user, StudyActivity activity) {
            this.user = user;
            this.activity = activity;
        }

        public Coffee() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public StudyActivity getActivity() {
            return activity;
        }

        public void setActivity(StudyActivity activity) {
            this.activity = activity;
        }

   //     public Date getDate() {
     //       return date;
       // }

        //public void setDate(Date date) {
          //  this.date = date;
        //}
    }
