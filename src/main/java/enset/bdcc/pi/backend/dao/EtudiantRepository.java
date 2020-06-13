package enset.bdcc.pi.backend.dao;


import enset.bdcc.pi.backend.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
@RepositoryRestResource
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant,Long> {
}
