
<template>
  <div style="padding:20px;font-family:Arial,Helvetica,sans-serif">
    <h2>FE Vue Demo</h2>
    <div style="margin-bottom:8px;">
      <a href='#' @click.prevent='goLogin'>Login</a> |
      <a href='#' @click.prevent='callApi'>User Management</a> |
      <a href='#' @click.prevent='logout'>Logout</a>
    </div>
    <div v-if='user'>
      <strong>Logged as:</strong> {{ user }}
    </div>
	<div class="container mt-4" id="myView" v-if="isAuthenticated">
		<h2>Demo DataTable Vue 3</h2>
		<button id="btnAdd" class="btn btn-primary mb-3" @click="openAdd()" v-if="role=='ROLE_ADMIN'">
		  Add More User
		</button>
		<DataTable 
		  ref="table"
		  :columns="columns"
		  class="table table-striped"
		  :ajax="{
			url: API_BASE+'/public/listUser',
			dataSrc: function (json) {
			  return json.data;
			}
		  }"
		>
		<template #actions="props">
			<!-- props.rowData is the full row object -->
			<button class="btn btn-sm btn-primary" @click="openEdit(props.rowData)" v-if="role=='ROLE_ADMIN'">
			  Edit
			</button>
			<button class="btn btn-sm btn-danger ms-2" @click="deleteRow(props.rowData)" v-if="role=='ROLE_ADMIN'">
			  Delete
			</button>
		  </template>
		</DataTable>
	  </div>
	<!-- Modal ADD -->
    <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true" ref="modalAddRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add User</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveAdd">
              <div class="mb-3">
                <label class="form-label">Name</label>
                <input v-model="username" class="form-control" />
              </div>
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input v-model="password" class="form-control" />
              </div>
			  <div class="mb-3">
				  <label class="form-label">ROLE</label>
					<select
					  v-model="roles"
					  class="form-control"
					>
					  <option disabled value="viewer">-- select role --</option>
					  <option v-for="role in roleData" :key="role" :value="'ROLE_'+role">
						{{ role }}
					  </option>
					</select>
			  </div>
              <button type="submit" class="btn btn-success">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
	
	<!-- Modal Update-->
    <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true" ref="modalUpdateRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit User</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveEdit">
              <div class="mb-3">
                <label class="form-label">Name</label>
                <input v-model="editUser.username" class="form-control" />
              </div>
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input v-model="editUser.password" class="form-control" />
              </div>
			  <div class="mb-3">
				  <label class="form-label">ROLE</label>
					<select
					  v-model="editUser.roles"
					  class="form-control"
					>
					  <option disabled value="">-- select role --</option>
					  <option v-for="role in roleData" :key="role" :value="'ROLE_'+role">
						{{ role }}
					  </option>
					</select>
			  </div>
              <button type="submit" class="btn btn-success">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import DataTable from 'datatables.net-vue3'
import DataTablesCore from 'datatables.net-bs5'

DataTable.use(DataTablesCore);

import { ref } from 'vue'
import api, { auth } from './api/http'



import 'bootstrap/dist/css/bootstrap.min.css'
import 'datatables.net-bs5/css/dataTables.bootstrap5.min.css'
import { Modal } from "bootstrap"
import { onMounted } from 'vue'
import $ from 'jquery'
import ModalForm from './components/FormInputData.vue'

import { jwtDecode } from 'jwt-decode'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8082';

const table = ref(null);
const user = ref(localStorage.getItem('username') || '')

function getUserRole() {
  const token = localStorage.getItem("accessToken")
  if (!token) return null
  try {
    return jwtDecode(token).roles[0]  // { sub, role, exp, ... }
  } catch (e) {
    return null
  }
}
const role = ref(getUserRole())

const isAuthenticated = ref(!!localStorage.getItem('accessToken'))

const editUser = ref({})
const addUser = ref({})

const username = ref('');
const password = ref('');
const roles = ref('');
const modalUpdateRef = ref(null)
let modalUpdateInstance = null

const modalAddRef = ref(null)
let modalAddInstance = null

function goLogin(){
  const username = prompt('username','')
  const password = prompt('password','')
  if(!username || !password) return
  auth.login(username,password).then(()=>{
  alert('logged in')}).catch(()=> alert('login failed'))
}

function isLoggedIn() {
  return !!localStorage.getItem("accessToken")
}

function callApi(){
	
	if(isLoggedIn()){
		$('#myView').show();
		location.reload();
	}else{
		$('#myView').hide();
		location.reload();
		alert("Please Login first");
	}
	
	
	
}

const roleData = ['ADMIN', 'USER']

const openAdd = () => {
  if (!modalAddInstance) {
    // Bootstrap modal instance
    modalAddInstance = new Modal(modalAddRef.value)
  }
  modalAddInstance.show()
}

const openEdit = (user) => {
  editUser.value = { ...user,roles:user.roles[0] }
  if (!modalUpdateInstance) {
    // Bootstrap modal instance
    modalUpdateInstance = new Modal(modalUpdateRef.value)
  }
  modalUpdateInstance.show()
}

function logout(){
  auth.logout(); user.value=''; localStorage.removeItem('username'); alert('logged out'); $('#myView').hide();
}
const columns = [
  { title: 'id', data: 'id',visible: false },
  { title: 'User Name', data: 'username' },
  { title: 'Password', data: 'password' },
  { title: 'Role', data: 'roles' },
  { data: null, title: "Actions", render: "#actions" } // để dùng slot
]
const saveAdd = (addUser) => {
  auth.register(username.value,password.value,roles.value).then((res)=>{ reloadTable();
  alert('completed')}).catch(()=> alert('Failure'))
  modalAddInstance.hide()
  //location.reload();
}
const saveEdit = () => {
  auth.update(editUser.value.id,editUser.value.username,editUser.value.password,editUser.value.roles).then(()=>{ reloadTable();
  alert('completed')}).catch(()=> alert('Failure'))
  modalUpdateInstance.hide()
 // location.reload();
}

const deleteRow = (rowData) => {
  auth.deleteUser(rowData.id).then(()=>{ reloadTable();
  alert('completed delete')}).catch(()=> alert('Failure'))
 // location.reload();
}

const reloadTable = () => {
  if (table.value) {
    table.value.dt.ajax.reload(null, false);
  }
};

</script>
<style>
@import "datatables.net-bs5";
</style>