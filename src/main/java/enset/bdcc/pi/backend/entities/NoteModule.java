package enset.bdcc.pi.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import enset.bdcc.pi.backend.entities.SemestreEtudiant;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@ToString
public class NoteModule implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    Module module;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "se_id")
    SemestreEtudiant semestreEtudiant;
    @OneToMany(mappedBy = "noteModule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<NoteElementModule> noteElementModules = new ArrayList<>();
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "note_normale")
    private float noteNormale;
    @Column(name = "note_delib")
    private float noteDeliberation;
    @Column(name = "note_ratt")
    private float noteRatt;
    @Column(name = "is_ratt")
    @JsonProperty("is_ratt")
    private boolean isRatt = false;
    @Column(name = "is_consistent")
    @JsonProperty("is_consistent")
    private boolean isConsistent = false;
    @Column(updatable = false, name = "created_at")
    @CreationTimestamp
    private Date createdAt; // initialize created date
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt; // initialize updated date

    public NoteModule(Module module, SemestreEtudiant semestreEtudiant) {
        this.module = module;
        this.semestreEtudiant = semestreEtudiant;
    }

}
