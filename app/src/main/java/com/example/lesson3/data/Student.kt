package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

@Entity(tableName = "students",
        indices = [Index("id"), Index("group_id", "id")],
        foreignKeys = [
            ForeignKey(
                entity = Group::class,
                parentColumns = ["id"],
                childColumns = ["group_id"],
                onDelete = ForeignKey.CASCADE
            )
        ]
)

data class Student(
    @SerializedName("id") @PrimaryKey val id: UUID = UUID.randomUUID(),
    @SerializedName("lastname") var lastname: String="",
    @SerializedName("firstname") var firstname: String="",
    @SerializedName("middlename") var middlename: String="",
    @SerializedName("birthDate") @ColumnInfo(name= "birth_date") var birthDate: Date=Date(),
    @SerializedName("groupID") @ColumnInfo(name="group_id") var groupID: UUID?=null,
    @SerializedName("phone") var phone: String="",
    @SerializedName("sex") var sex : Int=0
){
    val shortName
        get()=lastname+
                (if(firstname.length>0) {"${firstname.subSequence(1,2)}."}else "") +
                (if(middlename.length>0) {"${middlename.subSequence(1,2)}."}else "")
}
