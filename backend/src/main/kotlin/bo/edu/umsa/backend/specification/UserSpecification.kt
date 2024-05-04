package bo.edu.umsa.backend.specification

import bo.edu.umsa.backend.entity.User
import org.springframework.data.jpa.domain.Specification


class UserSpecification {
    companion object {

        fun kcUserKeyword(keyword: String): Specification<User> {
            return Specification { root, _, cb ->
                cb.or(
                    cb.like(cb.lower(root.get("email")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("firstName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("lastName")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("phone")), "%${keyword.lowercase()}%"),
                    cb.like(cb.lower(root.get("txHost")), "%${keyword.lowercase()}%"),
                )
            }
        }
        fun statusIsTrue(): Specification<User> {
            return Specification { root, _, cb ->
                cb.equal(root.get<User>("status"), true)
            }
        }
    }
}