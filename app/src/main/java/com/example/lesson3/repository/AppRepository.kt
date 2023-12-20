package com.example.lesson3.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.lesson3.API.APPEND_FACULTY
import com.example.lesson3.API.APPEND_GROUP
import com.example.lesson3.API.APPEND_STUDENT
import com.example.lesson3.API.DELETE_FACULTY
import com.example.lesson3.API.DELETE_GROUP
import com.example.lesson3.API.DELETE_STUDENT
import com.example.lesson3.API.ListAPI
import com.example.lesson3.API.ListConnection
import com.example.lesson3.API.PostFaculty
import com.example.lesson3.API.PostGroup
import com.example.lesson3.API.PostResult
import com.example.lesson3.API.PostStudent
import com.example.lesson3.API.UPDATE_FACULTY
import com.example.lesson3.API.UPDATE_GROUP
import com.example.lesson3.API.UPDATE_STUDENT
import com.example.lesson3.MyApplication
import com.example.lesson3.R
import com.example.lesson3.data.Faculties
import com.example.lesson3.data.Faculty
import com.example.lesson3.data.Group
import com.example.lesson3.data.Groups
import com.example.lesson3.data.ListOfFaculty
import com.example.lesson3.data.ListOfGroup
import com.example.lesson3.data.ListOfStudent
import com.example.lesson3.data.Student
import com.example.lesson3.data.Students
import com.example.lesson3.database.ListDatabase
import com.example.lesson3.myConsts.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.UUID

class AppRepository {
    companion object{
        private var INSTANCE: AppRepository?=null

        fun getInstance(): AppRepository {
            if (INSTANCE==null){
                INSTANCE= AppRepository()
            }
            return INSTANCE?:
            throw IllegalStateException("реп не иниц")
        }
    }

//    var listOfFaculty: MutableLiveData<ListOfFaculty?> = MutableLiveData()
    var faculty: MutableLiveData<Faculty> = MutableLiveData()
//    var listOfGroup: MutableLiveData<ListOfGroup?> = MutableLiveData()
    var group: MutableLiveData<Group> = MutableLiveData()
//    var listOfStudent: MutableLiveData<ListOfStudent?> = MutableLiveData()
    var student: MutableLiveData<Student> = MutableLiveData()


//    fun addFaculty(faculty: Faculty){
//        var listTmp = listOfFaculty.value
//        if (listTmp==null) listTmp= ListOfFaculty()
//        listTmp.items.add(faculty)
//        //
//        listOfFaculty.postValue(listTmp)
//        setCurrentFaculty(faculty)
//    }

    fun getFacultyPosition(faculty: Faculty): Int=listOfFaculty.value?.indexOfFirst {
        it.id==faculty.id } ?:-1

    fun getFacultyPosition()=getFacultyPosition(faculty.value?: Faculty())

    fun setCurrentFaculty(position:Int){
        if (position<0 || (listOfFaculty.value?.size!! <= position))
            return setCurrentFaculty(listOfFaculty.value!![position])
    }

    fun setCurrentFaculty(_faculty: Faculty){
        faculty.postValue(_faculty)
    }

    fun saveData(){

    }

    fun loadData(){
        fetchFaculties()
    }

    private fun updateFaculties(postFaculty: PostFaculty){
        listAPI.postFaculty(postFaculty)
            .enqueue(object:Callback<PostResult>{
                override fun onResponse(call: Call<PostResult>, response: Response<PostResult>) {
                    if (response.code()==200) fetchFaculties()
                }

                override fun onFailure(call: Call<PostResult>, t: Throwable) {
                    Log.d(TAG, "Ошибка записи факультета")
                }
            })
    }

    private fun updateGroups(postGroup: PostGroup){
        listAPI.postGroup(postGroup)
            .enqueue(object:Callback<PostResult>{
                override fun onResponse(call: Call<PostResult>, response: Response<PostResult>) {
                    if (response.code()==200) fetchGroups()
                }

                override fun onFailure(call: Call<PostResult>, t: Throwable) {
                    Log.d(TAG, "Ошибка записи групп")
                }
            })
    }

    private fun updateStudents(postStudent: PostStudent){
        listAPI.postStudent(postStudent)
            .enqueue(object:Callback<PostResult>{
                override fun onResponse(call: Call<PostResult>, response: Response<PostResult>) {
                    if (response.code()==200) fetchStudents()
                }

                override fun onFailure(call: Call<PostResult>, t: Throwable) {
                    Log.d(TAG, "Ошибка записи студентов")
                }
            })
    }

//    fun updateFaculty(faculty:Faculty){
//        val position = getFacultyPosition(faculty)
//        if (position <0) addFaculty(faculty)
//        else {
//            val listTmp=listOfFaculty.value!!
//            listTmp.items[position]=faculty
//            Log.d(TAG, "Изменен факультет ${faculty.name}")
//            listOfFaculty.postValue(listTmp)
//        }
//    }

//    fun deleteFaculty(faculty: Faculty){
//        val listTmp = listOfFaculty.value!!
//        if (listTmp.items.remove(faculty)){
//            Log.d(TAG, "Удален факультет ${faculty.name}")
//            listOfFaculty.postValue(listTmp)
//        }
//        setCurrentFaculty(0)
//    }

//    fun saveData(){
//        val context =  MyApplication.context
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        sharedPreferences.edit().apply{
//            val gson = Gson()
//            val lst = listOfFaculty.value?.items ?: listOf()
//            var jsonString= gson.toJson(lst)
//            Log.d(TAG, "сохранение $jsonString")
//            putString(context.getString(R.string.preference_key_faculty_list),
//                jsonString)
//            jsonString=gson.toJson(listOfGroup.value?.items ?: listOf<Group>())
//            putString(context.getString(R.string.preference_key_group_list),
//                jsonString)
//            jsonString=gson.toJson(listOfGroup.value?.items ?: listOf<Student>())
//            putString(context.getString(R.string.preference_key_student_list),
//                jsonString)
//                .apply()
//        }
//    }

//    fun loadData(){
//        val context =  MyApplication.context
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        sharedPreferences.apply {
//            val jsonString= getString("lesson3.preference.key.faculty_list", null)
//            if (jsonString!=null){
//                Log.d(TAG, "чтение $jsonString")
//                val listType = object : TypeToken<List<Faculty>>() {}.type
//                val tempList = Gson().fromJson<List<Faculty>>(jsonString, listType)
//                val temp = ListOfFaculty()
//                temp.items = tempList.toMutableList()
//                Log.d(TAG, "загрузка ${temp.toString()}")
//                listOfFaculty.postValue(temp)
//            }
//            val jsonStringG= getString("lesson3.preference.key.group_list", null)
//            if (jsonStringG!=null){
//                val listTypeG= object : TypeToken<List<Group>>() {}.type
//                val tempListG = Gson().fromJson<List<Group>>(jsonStringG, listTypeG)
//                val tempG = ListOfGroup()
//                tempG.items = tempListG.toMutableList()
//                Log.d(TAG, "загрузка ${tempG.toString()}")
//                listOfGroup.postValue(tempG)
//            }
//            val jsonStringS= getString("lesson3.preference.key.student_list", null)
//            if (jsonStringS!=null){
//                val listTypeS = object : TypeToken<List<Student>>() {}.type
//                val tempListS = Gson().fromJson<List<Student>>(jsonStringS, listTypeS)
//                val tempS = ListOfStudent()
//                tempS.items = tempListS.toMutableList()
//                Log.d(TAG, "загрузка ${tempS.toString()}")
//                listOfStudent.postValue(tempS)
//            }
//        }
//    }

//    fun addGroup(group: Group){
//        val listTmp = (listOfGroup.value ?: ListOfGroup()).apply {
//            items.add(group)
//        }
//        listOfGroup.postValue(listTmp)
//        setCurrentGroup(group)
//    }

    fun getGroupPosition(group: Group): Int=listOfGroup.value?.indexOfFirst {
        it.id==group.id } ?:-1

    fun getGroupPosition()=getGroupPosition(group.value?: Group())

    fun setCurrentGroup(position:Int){
        if (listOfGroup.value==null || position<0 ||
            (listOfGroup.value?.size!! <=position))
            return setCurrentGroup(listOfGroup.value!![position])
    }

    fun setCurrentGroup(_group: Group){
        group.postValue(_group)
    }

//    fun updateGroup(group:Group){
//        val position = getGroupPosition(group)
//        if (position <0) addGroup(group)
//        else {
//            val listTmp=listOfGroup.value!!
//            listTmp.items[position]=group
//            Log.d(TAG, "Изменен факультет ${group.name}")
//            listOfGroup.postValue(listTmp)
//        }
//    }

//    fun deleteGroup(group: Group){
//        val listTmp = listOfGroup.value ?: ListOfGroup()
//        if (listTmp.items.remove(group)){
//            Log.d(TAG, "Удален факультет ${group.name}")
//            listOfGroup.postValue(listTmp)
//        }
//        setCurrentGroup(0)
//    }

    val facultyGroups
        get()= listOfGroup.value?.filter { it.facultyID == (faculty.value?.id ?: 0) }?.sortedBy { it.name }?: listOf()

//    fun getFacultyGroup(facultyID: UUID) =
//        (listOfGroup.value?.filter { it.facultyID == facultyID }?.sortedBy { it.name }?: listOf())

//    fun addStudent(student: Student){
//        val listTmp = (listOfStudent.value ?: ListOfStudent()).apply {
//            items.add(student)
//        }
//        listOfStudent.postValue(listTmp)
//        setCurrentStudent(student)
//    }

    fun getStudentPosition(student: Student): Int=listOfStudent.value?.indexOfFirst {
        it.id==student.id } ?:-1

    fun getStudentPosition()=getStudentPosition(student.value?: Student())

    fun setCurrentStudent(position:Int){
        if (listOfStudent.value==null || position<0 ||
            (listOfStudent.value?.size!! <=position))
            return setCurrentStudent(listOfStudent.value!![position])
    }

    fun setCurrentStudent(_student: Student){
        student.postValue(_student)
    }

//    fun updateStudent(student: Student){
//        val position = getStudentPosition(student)
//        if (position <0) addStudent(student)
//        else {
//            val listTmp=listOfStudent.value!!
//            listTmp.items[position]=student
//            Log.d(TAG, "Изменен студент ${student.shortName}")
//            listOfStudent.postValue(listTmp)
//        }
//    }

//    fun deleteStudent(student: Student){
//        val listTmp = listOfStudent.value ?: ListOfStudent()
//        if (listTmp.items.remove(student)){
//            Log.d(TAG, "Удален студент ${student.shortName}")
//            listOfStudent.postValue(listTmp)
//        }
//        setCurrentStudent(0)
//    }

//    val groupStudent
//        get()= listOfStudent.value?.items?.filter { it.groupID == (group.value?.id ?: 0) }?.sortedBy { it.shortName }?: listOf()

    private var listAPI = ListConnection.getClient().create(ListAPI::class.java)

    fun fetchFaculties(){
        listAPI.getFaculties().enqueue(object : Callback<Faculties> {
            override fun onFailure(call: Call<Faculties>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка факультетов", t)
            }
            override fun onResponse(call: Call<Faculties>, response: Response<Faculties>) {
                if (response.code()==200) {
                    val faculties = response.body()
                    val items = faculties?.items ?: emptyList()
                    Log.d(TAG, "Получен список факультетов $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllFaculty()
                        for (f in items) {
                            listDB.insertFaculty(f)
                        }
                    }
                    fetchGroups()
                }
            }
        })
    }

    fun fetchGroups(){
        listAPI.getGroups().enqueue(object : Callback<Groups> {
            override fun onFailure(call: Call<Groups>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка Групп", t)
            }
            override fun onResponse(call: Call<Groups>, response: Response<Groups>) {
                if (response.code()==200) {
                    val groups = response.body()
                    val items = groups?.items ?: emptyList()
                    Log.d(TAG, "Получен список групп $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllGroups()
                        for (f in items) {
                            listDB.insertGroup(f)
                        }
                    }
                    fetchStudents()
                }
            }
        })
    }

    fun fetchStudents(){
        listAPI.getStudents().enqueue(object : Callback<Students> {
            override fun onFailure(call: Call<Students>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка студентов", t)
            }
            override fun onResponse(call: Call<Students>, response: Response<Students>) {
                if (response.code()==200) {
                    val students = response.body()
                    val items = students?.items ?: emptyList()
                    Log.d(TAG, "Получен список студентов $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllStudents()
                        for (f in items) {
                            listDB.insertStudent(f)
                        }
                    }
                }
            }
        })
    }


    fun getGroupStudents(groupID: UUID) =
        (listOfStudent.value?.filter { it.groupID == groupID }?.sortedBy { it.shortName }?: listOf())

    private val listDB by lazy {OfflineDBRepository(ListDatabase.getDatabase(MyApplication.context).listDAO())}

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    fun onDestroy(){
        myCoroutineScope.cancel()
    }

    val listOfFaculty: LiveData<List<Faculty>> = listDB.getFaculty().asLiveData()

    /*fun addFaculty(faculty: Faculty){
        myCoroutineScope.launch {
            listDB.insertFaculty(faculty)
            setCurrentFaculty(faculty)
        }
    }

    fun updateFaculty(faculty: Faculty){
        addFaculty(faculty)
    }

    fun deleteFaculty(faculty: Faculty){
        myCoroutineScope.launch {
            listDB.deleteFaculty(faculty)
            setCurrentFaculty(0)
        }
    }*/
    fun addFaculty(faculty: Faculty) {
        updateFaculties(PostFaculty(APPEND_FACULTY, faculty))
    }

    fun deleteFaculty(faculty: Faculty) {
        updateFaculties(PostFaculty(DELETE_FACULTY, faculty))
    }

    fun updateFaculty(faculty: Faculty) {
        updateFaculties(PostFaculty(UPDATE_FACULTY, faculty))
    }

    fun addGroup(group: Group) {
        updateGroups(PostGroup(APPEND_GROUP, group))
    }

    fun deleteGroup(group: Group) {
        updateGroups(PostGroup(DELETE_GROUP, group))
    }

    fun updateGroup(group: Group) {
        updateGroups(PostGroup(UPDATE_GROUP, group))
    }

    fun addStudents(student: Student) {
        updateStudents(PostStudent(APPEND_STUDENT, student))
    }

    fun deleteStudents(student: Student) {
        updateStudents(PostStudent(DELETE_STUDENT, student))
    }

    fun updateStudents(student: Student) {
        updateStudents(PostStudent(UPDATE_STUDENT, student))
    }



    val listOfGroup: LiveData<List<Group>> = listDB.getAllGroups().asLiveData()

    /*fun addGroup(group: Group){
        myCoroutineScope.launch {
            listDB.insertGroup(group)
            setCurrentGroup(group)
        }
    }

    fun updateGroup(group: Group){
        addGroup(group)
    }

    fun deleteGroup(group: Group){
        myCoroutineScope.launch {
            listDB.deleteGroup(group)
            setCurrentGroup(0)
        }
    }*/

    val listOfStudent: LiveData<List<Student>> = listDB.getAllStudents().asLiveData()

    fun addStudent(student: Student){
        myCoroutineScope.launch {
            listDB.insertStudent(student)
            setCurrentStudent(student)
        }
    }

    fun updateStudent(student: Student){
        addStudent(student)
    }

    fun deleteStudent(student: Student){
        myCoroutineScope.launch {
            listDB.deleteStudent(student)
            setCurrentStudent(0)
        }
    }

}





















