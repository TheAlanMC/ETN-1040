package bo.edu.umsa.backend.entity

import bo.edu.umsa.backend.util.AuthUtil
import bo.edu.umsa.backend.util.HttpUtil
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "task", schema = "public")
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    var taskId: Int = 0

    @Column(name = "project_id")
    var projectId: Int = 0

    @Column(name = "task_status_id")
    var taskStatusId: Int = 0

    @Column(name = "task_priority_id")
    var taskPriorityId: Int = 0

    @Column(name = "task_name")
    var taskName: String = ""

    @Column(name = "task_description")
    var taskDescription: String = ""

    @Column(name = "task_due_date")
    var taskDueDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "task_end_date")
    var taskEndDate: Timestamp? = null

    @Column(name = "task_rating")
    var taskRating: Int = 0

    @Column(name = "task_rating_comment")
    var taskRatingComment: String = ""

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "tx_date")
    var txDate: Timestamp = Timestamp(System.currentTimeMillis())

    @Column(name = "tx_user")
    var txUser: String = AuthUtil.getEmailFromAuthToken() ?: "admin"

    @Column(name = "tx_host")
    var txHost: String = HttpUtil.getRequestHost() ?: "localhost"

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    var taskAssignees: List<TaskAssignee>? = null

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    var taskComments: List<TaskComment>? = null

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    var taskFiles: List<TaskFile>? = null

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    var taskHistories: List<TaskHistory>? = null

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    var replacedParts: List<ReplacedPart>? = null

    @ManyToOne
    @JoinColumn(name = "task_status_id", insertable = false, updatable = false)
    var taskStatus: TaskStatus? = null

    @ManyToOne
    @JoinColumn(name = "task_priority_id", insertable = false, updatable = false)
    var taskPriority: TaskPriority? = null

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    var project: Project? = null

}
