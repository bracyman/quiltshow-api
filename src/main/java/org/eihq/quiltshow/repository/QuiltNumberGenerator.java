package org.eihq.quiltshow.repository;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Creates a new unique show number when a quilt is created
 * @author bracyman
 *
 */
public class QuiltNumberGenerator implements IdentifierGenerator {
	
	private static Integer numberGenerator = 1001;

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		// TODO Auto-generated method stub
		return numberGenerator++;
	}

}
