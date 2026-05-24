package io.github.gluedtokeyboard.vcs.api.common.idgenerator;

import io.github.gluedtokeyboard.vcs.util.Uuid;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * A UUIDv7 generator.
 */
public class UuidV7Generator implements IdentifierGenerator {
    /**
     * Generate a new UUIDv7.
     *
     * @param session The session from which the request originates
     * @param object  the entity or collection (idbag) for which the id is being generated
     * @return a new UUIDv7
     * @throws HibernateException Indicates trouble generating the identifier
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return Uuid.uuidV7();
    }
}
