package net.soomsam.zirmegghuette.zars.persistence.entity;

import jdave.Specification;
import jdave.contract.SerializableContract;
import jdave.junit4.JDaveRunner;

import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class EntityContractSpecification extends Specification<GroupReservation>{
	public class GroupReservationEntity {
		private GroupReservation groupReservation;
			
		public GroupReservation create() {
			groupReservation = new GroupReservation();
			return groupReservation;
		}
		
		public void isSerializable() {
            specify(groupReservation, should.not().satisfy(new SerializableContract()));
        }
	}
}
