package org.svomz.commons.persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;

public class RepositoryUnitTest {

  private EntityManagerFactory emf;
  private EntityManager em;

  @Before
  public void setUp() throws ClassNotFoundException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    this.emf = Persistence.createEntityManagerFactory("pu-unittests");
    this.em = this.emf.createEntityManager();
  }

  @Test
  public void createShouldWork() throws EntityNotFoundException {
    Person person = this.generatePerson();

    PersonRepository repository = new PersonRepository(this.em);
    repository.create(person);

    Assert.assertEquals(0, repository.findAll().size());

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertEquals(1, repository.findAll().size());
  }

  private Person generatePerson() {
    Person person = new Person();
    person.setFirstname("John");
    person.setLastname("Doe");
    return person;
  }

  @Test
  public void findShouldWork() throws EntityNotFoundException {
    Person person = this.generatePerson();

    PersonRepository repository = new PersonRepository(this.em);
    repository.create(person);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Person persistedPerson = repository.find(person.getId());
    Assert.assertNotNull(persistedPerson);
    Assert.assertEquals(person.getId(), persistedPerson.getId());
    Assert.assertEquals(person.getFirstname(), persistedPerson.getFirstname());
    Assert.assertEquals(person.getLastname(), persistedPerson.getLastname());
  }

  @Test(expected = EntityNotFoundException.class)
  public void findWontWork() throws EntityNotFoundException {
    Person person = this.generatePerson();

    PersonRepository repository = new PersonRepository(this.em);
    repository.create(person);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    repository.find(person.getId() + 1);
  }

  @Test
  public void updateShouldWork() throws EntityNotFoundException {
    Person person = this.generatePerson();

    PersonRepository repository = new PersonRepository(this.em);
    repository.create(person);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    person.setFirstname("Robert");
    repository.update(person);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Person persistedPerson = repository.find(person.getId());
    Assert.assertNotNull(persistedPerson);
    Assert.assertEquals(person.getFirstname(), persistedPerson.getFirstname());
  }

  @Test
  public void deleteShouldWork() {
    Person person = this.generatePerson();

    PersonRepository repository = new PersonRepository(this.em);
    repository.create(person);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertEquals(1, repository.findAll().size());

    repository.delete(person);
    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertEquals(0, repository.findAll().size());
  }

  @After
  public void tearDown() {
    this.em.close();
    this.emf.close();
  }

  public static class PersonRepository extends AbstractJpaRepository<Person, Long> {

    public PersonRepository(EntityManager entityManager) {
      super(entityManager);
    }

  }

}
